/*
 * Copyright (C) 2015 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.welteninstituut.tel.la.importers;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public abstract class ImportTask implements DeferredTask {

	private static final long serialVersionUID = 1L;

	private final boolean useWorkingDays;
	private final LocalTime starttime;
	private final LocalTime endtime;

	protected ImportTask() {
		useWorkingDays = Configuration.getAsBoolean(Configuration.WORKING_DAYS, true);
		starttime = getTime(Configuration.START_TIME);
		endtime = getTime(Configuration.END_TIME);
	}

	protected OauthServiceAccount getAccount(final int serviceId, final String accountId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(OauthServiceAccount.class);
			try {
				q.setFilter("serviceId == serviceIdParam");
				q.setFilter("accountId == accountIdParam");
				q.declareParameters("Integer serviceIdParam, String accountIdParam");

				@SuppressWarnings("unchecked")
				List<OauthServiceAccount> result = (List<OauthServiceAccount>) q.execute(serviceId, accountId);
				return result.isEmpty() ? null : result.get(0);
			} finally {
				q.closeAll();
			}

		} finally {
			pm.close();
		}
	}

	/**
	 * Checks if the specified time is allowed according to the configuration
	 * settings.
	 *
	 * @param dateTime
	 *            the date time
	 * @return true, if is time allowed
	 */
	protected boolean isTimeAllowed(final DateTime dateTime) {
		if (useWorkingDays && dateTime.dayOfWeek().get() > 5) {
			return false;
		}

		LocalTime time = dateTime.toLocalTime();

		if (starttime != null && time.isBefore(starttime)) {
			return false;
		}

		if (endtime != null && time.isAfter(endtime)) {
			return false;
		}

		return true;
	}

	private LocalTime getTime(final String key) {
		String value = Configuration.get(key);
		return value == null ? null : LocalTime.parse(value);
	}

	public static void scheduleTask(final DeferredTask task) {
		// Add the task to the default queue.
		Queue queue = QueueFactory.getDefaultQueue();

		queue.add(TaskOptions.Builder.withPayload(task));
	}

}