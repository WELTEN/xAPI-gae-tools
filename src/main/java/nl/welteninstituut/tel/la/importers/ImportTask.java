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

import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public abstract class ImportTask implements DeferredTask {

	private static final long serialVersionUID = 1L;

	public static void scheduleTask(final DeferredTask task) {
		// Add the task to the default queue.
		Queue queue = QueueFactory.getDefaultQueue();
	
		queue.add(TaskOptions.Builder.withPayload(task));
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
				List<OauthServiceAccount> result = (List<OauthServiceAccount>) q.execute(serviceId,
						accountId);
				return result.isEmpty() ? null : result.get(0);
			} finally {
				q.closeAll();
			}

		} finally {
			pm.close();
		}
	}

}