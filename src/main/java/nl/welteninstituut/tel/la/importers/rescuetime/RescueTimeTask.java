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
package nl.welteninstituut.tel.la.importers.rescuetime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.importers.ImportTask;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This task fetches the RescueTime data for a user. Data is processed per day.
 * 
 * @author Harrie Martens
 *
 */
public class RescueTimeTask extends ImportTask {

	private static final long serialVersionUID = 2L;
	private static final Logger log = Logger.getLogger(RescueTimeTask.class.getName());

	private static final String URI_FORMAT = "https://www.rescuetime.com/anapi/data?key=%s&pv=interval&rs=minute&rb=%2$s&re=%2$s&format=json";
	private static final String XAPI_RESCUETIME_FORMAT = "{\"timestamp\":\"%s\","
			+ "\"actor\": {\"objectType\": \"Agent\",\"mbox\":\"mailto:%s\"},"
			+ "\"verb\":{\"id\":\"http://activitystrea.ms/schema/1.0/access\","
			+ "\"display\":{\"en-US\":\"indicates the user accessed something\"}},"
			+ "\"object\":{\"objectType\":\"Activity\",\"id\":\"%s\",\"definition\":{\"name\":{\"en-US\":\"name of the application\"},"
			+ "\"description\":{\"en-US\":\"description of the application\"},\"type\":\"http://activitystrea.ms/schema/1.0/application\"}},"
			+ "\"result\":{\"duration\":\"PT%d.00S\"}}";

	private String accountId;
	private DateTime start;

	public RescueTimeTask() {
	}

	public RescueTimeTask(final String accountId) {
		this.accountId = accountId;
	}

	private RescueTimeTask(final String accountId, final DateTime start) {
		this(accountId);
		this.start = start;
	}

	@Override
	public void run() {
		OauthServiceAccount account = getAccount(AccountJDO.RESCUETIMECLIENT, accountId);
		if (account != null) {

			if (start == null) {

				if (account.getLastSynced() == null) {
					String startDate = Configuration.get(Configuration.STARTDATE);
					if (startDate != null) {
						start = new DateTime(startDate + "T00:00");
					} else {
						log.severe(Configuration.STARTDATE + " is missing from configuration");
					}
				} else {
					start = new DateTime(account.getLastSynced());
				}
			}

			System.out.println("Starting @ " + start);

			if (start != null) {
				LocalDate localDate = start.toLocalDate();

				try {
					JSONObject data = getData(account.getAccessToken(), localDate);

					JSONArray rows = data.getJSONArray("rows");
					String mbox = null;

					if (rows.length() > 0) {
						AccountJDO pa = AccountManager.getAccount(account.getPrimaryAccount());
						mbox = pa != null ? pa.getEmail() : null;
					}

					Row row = null;
					for (int i = 0; i < rows.length(); i++) {
						row = new Row(rows.getJSONArray(i));
						if (row.getDate().isEqual(start) || row.getDate().isAfter(start)) {

							String xapi = String.format(XAPI_RESCUETIME_FORMAT, row.getDate(), mbox, row.getActivity(),
									row.getTimeSpent());
							System.out.println(xapi);	
						}
					}

					boolean isNotToday = localDate.isBefore(new LocalDate());

					if (isNotToday) {
						start = localDate.plusDays(1).toDateTimeAtStartOfDay();
					} else {
						if (row != null) {
							start = row.getDate().plusSeconds(1);
						}
					}

					account.setLastSynced(start.toDate());
					OauthServiceAccountManager.updateOauthServiceAccount(account);

					if (isNotToday) {
						ImportTask.scheduleTask(new RescueTimeTask(accountId, start));
					}

				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}

			}

		} else {
			log.severe("no RescueTime service account found for " + accountId);
		}
	}

	private JSONObject getData(final String apiKey, final LocalDate date) throws MalformedURLException, JSONException,
			IOException {
		String uri = String.format(URI_FORMAT, apiKey, date.toString());

		JSONObject data = new JSONObject(readURL(new URL(uri)));
		if (data.has("error")) {
			throw new JSONException(data.getString("error"));
		}

		return data;
	}

	private String readURL(URL url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		InputStream is = connection.getInputStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	private class Row {
		private final DateTime date;
		private final long timeSpent;
		private final String activity;
		private final String category;

		private Row(final JSONArray data) throws JSONException {
			date = new DateTime(data.getString(0));
			timeSpent = data.getLong(1);
			activity = data.getString(3);
			category = data.getString(4);
		}

		protected DateTime getDate() {
			return date;
		}

		protected long getTimeSpent() {
			return timeSpent;
		}

		protected String getActivity() {
			return activity;
		}

		protected String getcategory() {
			return category;
		}
	}

}
