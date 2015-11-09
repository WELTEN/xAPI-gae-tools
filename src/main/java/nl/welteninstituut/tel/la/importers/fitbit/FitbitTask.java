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
package nl.welteninstituut.tel.la.importers.fitbit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.importers.ImportTask;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;
import nl.welteninstituut.tel.util.StringPool;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.apphosting.api.ApiProxy;

/**
 * The FitbitTask fetches the Fitbit heart rate and step count data for the
 * specified user. A single task retrieves and processes max two hours of data
 * starting at the <code>OauthServiceAccount.lastSynced</code> date. The task
 * starts a subsequent task for fetching the next period of data. Data is
 * fetched until the last synchronization time of the fitbit device.
 * 
 * @author Harrie Martens
 *
 */
public class FitbitTask extends ImportTask {

	private static final long serialVersionUID = 2L;
	private static final Logger log = Logger.getLogger(FitbitTask.class.getName());

	private String accountId;
	private DateTime start;
	private DateTime deviceLastSynced;

	public FitbitTask() {
	}

	public FitbitTask(final String accountId) {
		this.accountId = accountId;
	}

	private FitbitTask(final String accountId, final DateTime start, final DateTime deviceLastSynced) {
		this(accountId);
		this.start = start;
		this.deviceLastSynced = deviceLastSynced;
	}

	@Override
	public void run() {
		OauthServiceAccount account = getAccount(AccountJDO.FITBITCLIENT, accountId);
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
					start = new DateTime(account.getLastSynced()).withSecondOfMinute(0).withMillisOfSecond(0)
							.plusMinutes(1);
				}
			}

			if (deviceLastSynced == null) {
				deviceLastSynced = getDeviceLastSynced(account);
			}

			if (start != null && deviceLastSynced != null && start.isBefore(deviceLastSynced)) {

				DateTime end = start.plusHours(4);
				if (end.isAfter(deviceLastSynced)) {
					end = new DateTime(deviceLastSynced).withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(1);
				}

				// check if the period spans to next day
				if (end.getDayOfMonth() != start.getDayOfMonth()) {
					// if so reset to start of day because heart rate can only
					// be retrieved for a single calendar day
					end = end.withTimeAtStartOfDay();
				}

				try {

					System.out.println("processing from " + start + " to " + end);

					JSONObject stepCountData = new JSONObject(readURL(getStepcountURL(start, end), account));
					JSONObject heartRateData = new JSONObject(readURL(getHeartrateURL(start, end), account));

					if (!stepCountData.has("errors") && !heartRateData.has("errors")) {
						AccountJDO pa = AccountManager.getAccount(account.getPrimaryAccount());
						String mbox = pa != null ? pa.getEmail() : null;
						
						System.out.println("mbox: " + mbox);
						
						String xapiTemplate = "{\"timestamp\":\"%s\","
								+ "\"actor\": {\"objectType\": \"Agent\",\"mbox\":\"mailto:"
								+ mbox
								+ "\"},"
								+ "\"verb\":{\"id\":\"https://brindlewaye.com/xAPITerms/verbs/walked\","
								+ "\"display\":{\"en-US\":\"indicates the user walked number of steps\"}},"
								+ "\"object\":{\"objectType\":\"Activity\",\"id\":\"StepCount\",\"definition\":{\"name\":{\"en-US\":\"step count\"},"
								+ "\"description\":{\"en-US\":\"step count\"},\"type\":\"http://activitystrea.ms/schema/1.0/event\"}},"
								+ "\"result\":{\"response\":\"%d\"}}";
						
						processData(stepCountData, "activities-steps", xapiTemplate);
						
						xapiTemplate = "{\"timestamp\":\"%s\","
								+ "\"actor\": {\"objectType\": \"Agent\",\"mbox\":\"mailto:"
								+ mbox
								+ "\"},"
								+ "\"verb\":{\"id\":\"http://adlnet.gov/expapi/verbs/experienced\","
								+ "\"display\":{\"en-US\":\"indicates the user experienced something\"}},"
								+ "\"object\":{\"objectType\":\"Activity\",\"id\":\"HeartRate\",\"definition\":{\"name\":{\"en-US\":\"heart rate\"},"
								+ "\"description\":{\"en-US\":\"heart rate\"},\"type\":\"http://activitystrea.ms/schema/1.0/event\"}},"
								+ "\"result\":{\"response\":\"%d\"}}";
						
						processData(heartRateData, "activities-heart", xapiTemplate);
					}

					// store time of last block data that was
					// synchronized
					System.out.println("setting last synced to " + end);
					account.setLastSynced(end.toDate());
					OauthServiceAccountManager.updateOauthServiceAccount(account);

					System.out.println("remaining: " + ApiProxy.getCurrentEnvironment().getRemainingMillis());

					// Daisy chain task for next period
					if (end.isBefore(deviceLastSynced)) {
						ImportTask.scheduleTask(new FitbitTask(accountId, end, deviceLastSynced));
					}
				} catch (JSONException | IOException ex) {
					log.log(Level.SEVERE, "task aborted", ex);
				}
			}

		} else {
			log.severe("no fitbit service account found for " + accountId);
		}

	}
	
	private void processData(final JSONObject json, final String name, final String xapiTemplate) throws JSONException {

		if (json.has(name + "-intraday")) {
			System.out.println(name + "-intraday found");
			JSONArray dataset = json.getJSONObject(name + "-intraday").getJSONArray("dataset");

			System.out.println("# datapoints: " + dataset.length());

			if (dataset.length() > 0) {

				String fitbitDate = getDateFromFitbit(json, name);

				for (int i = 0; i < dataset.length(); i++) {
					JSONObject datapoint = dataset.getJSONObject(i);
					createStatement(xapiTemplate, fitbitDate, datapoint.getString("time"),
							datapoint.getInt("value"));
				}

			}
		}
	}

	private DateTime getDeviceLastSynced(final OauthServiceAccount account) {
		DateTime result = null;

		JSONArray devices;
		try {
			devices = new JSONArray(readURL(new URL("https://api.fitbit.com/1/user/-/devices.json"), account));
			JSONObject device = null;
			if (devices.length() == 1) {
				device = devices.getJSONObject(0);
			} else {
				for (int i = 0; i < devices.length(); i++) {
					if ("Charge HR".equals(devices.getJSONObject(i).getString("deviceVersion"))) {
						device = devices.getJSONObject(i);
						break;
					}
				}
			}

			if (device == null) {
				log.severe("No Fitbit device found for user");
			} else {
				result = new DateTime(device.getString("lastSyncTime"));
			}
		} catch (JSONException | IOException e) {
			log.log(Level.SEVERE, "Error fetching sync date for fitbit device", e);
		}

		System.out.println("device last synced @ " + result);
		return result;
	}

	private String getDateFromFitbit(final JSONObject data, final String name) throws JSONException {
		JSONArray ah = data.getJSONArray(name);
		String result = ah.getJSONObject(0).getString("dateTime");

		if ("today".equals(result)) {
			result = new LocalDate().toString();
		}

		return result;
	}

	private String createStatement(final String xapiTemplate, final String date, final String time,
			final int heartRate) {
		DateTime logDate = new DateTime(date + "T" + time);
		return String.format(xapiTemplate, logDate.toString(), heartRate);
	}

	private URL getHeartrateURL(DateTime start, DateTime end) throws MalformedURLException {
		StringBuilder sb = new StringBuilder("https://api.fitbit.com/1/user/-/activities/heart/date/");
		sb.append(DateTimeFormat.forPattern("yyyy-MM-dd").print(start));
		sb.append("/1d/1sec/time/");
		DateTimeFormatter fitbitTimePattern = DateTimeFormat.forPattern("HH:mm");
		sb.append(fitbitTimePattern.print(start));
		sb.append(StringPool.SLASH);
		String endTime = fitbitTimePattern.print(end);
		sb.append(endTime.equals("00:00") ? "23:59" : endTime);
		sb.append(".json");

		return new URL(sb.toString());
	}

	private URL getStepcountURL(DateTime start, DateTime end) throws MalformedURLException {
		StringBuilder sb = new StringBuilder("https://api.fitbit.com/1/user/-/activities/steps/date/");
		sb.append(DateTimeFormat.forPattern("yyyy-MM-dd").print(start));
		sb.append("/1d/1min/time/");
		DateTimeFormatter fitbitTimePattern = DateTimeFormat.forPattern("HH:mm");
		sb.append(fitbitTimePattern.print(start));
		sb.append(StringPool.SLASH);
		String endTime = fitbitTimePattern.print(end);
		sb.append(endTime.equals("00:00") ? "23:59" : endTime);
		sb.append(".json");

		return new URL(sb.toString());
	}

	private String readURL(final URL url, final OauthServiceAccount account) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// InputStream is = url.openStream();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Bearer " + account.getAccessToken());

		InputStream is = connection.getInputStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}

		int responseCode = connection.getResponseCode();
		if (responseCode == 401) {
			// refresh access token using refresh token
			exchangeCodeForAccessToken(account);

			baos = new ByteArrayOutputStream();
			// InputStream is = url.openStream();

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Bearer " + account.getAccessToken());

			is = connection.getInputStream();
			while ((r = is.read()) != -1) {
				baos.write(r);
			}

		}
		System.out.println(new String(baos.toByteArray()));
		return new String(baos.toByteArray());
	}

	private void exchangeCodeForAccessToken(final OauthServiceAccount account) {
		OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(account.getServiceId());
		String client_id = jdo.getClient_id();
		String client_secret = jdo.getClient_secret();

		System.out.println("refresh token: " + account.getRefreshToken());

		String result = postUrl("https://api.fitbit.com/oauth2/token", "grant_type=refresh_token&refresh_token="
				+ account.getRefreshToken(), client_id + ":" + client_secret);

		try {
			JSONObject resultJson = new JSONObject(result);

			System.out.println("refreshing gives " + result);

			String accessToken = resultJson.getString("access_token");
			String refreshToken = resultJson.getString("refresh_token");

			System.out.println("new access token acquired " + accessToken);
			System.out.println("new refresh token acquired " + refreshToken);

			account.setAccessToken(accessToken);
			account.setRefreshToken(refreshToken);
			OauthServiceAccountManager.updateOauthServiceAccount(account);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String postUrl(String url, String data, String authorization) {
		StringBuilder result = new StringBuilder();

		try {
			URLConnection conn = new URL(url).openConnection();
			// conn.setConnectTimeout(30);
			conn.setDoOutput(true);

			if (authorization != null)
				conn.setRequestProperty("Authorization",
						"Basic " + new String(new Base64().encode(authorization.getBytes())));
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			wr.close();
			rd.close();

		} catch (Exception e) {
		}

		return result.toString();
	}


}
