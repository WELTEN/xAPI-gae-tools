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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.la.tasks.GenericBean;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;
import nl.welteninstituut.tel.util.StringPool;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.apphosting.api.ApiProxy;

/**
 * The FitbitTask fetches the Fitbit heart rate data for the specified user. A
 * single task retrieves and processes max one hour of data starting at the
 * <code>OauthServiceAccount.lastSynced</code> date. The task starts a
 * subsequent task for fetching the next hour of data.
 * 
 * @author Harrie Martens
 *
 */
public class FitbitTask extends GenericBean {
	private static final Logger log = Logger.getLogger(FitbitTask.class.getName());

	private String accountId;

	public FitbitTask() {

	}

	public FitbitTask(final String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public void run() {
		OauthServiceAccount account = getAccount(accountId);
		if (account != null) {
			System.out.println("Running task for user " + accountId + " last synced @ " + account.getLastSynced());
			DateTime start = null;;
			if (account.getLastSynced() == null) {
				String startDate = Configuration.get(Configuration.FITBIT_STARTDATE);
				if (startDate != null) {
					start = new DateTime(startDate + "T00:00");
				} else {
					log.severe(Configuration.FITBIT_STARTDATE + " is missing from configuration");
				}
			} else {
				start = new DateTime(account.getLastSynced());
			}
			
			if (start != null) {
				DateTime end = start.plus(Duration.standardHours(1));
				if (end.isBeforeNow()) {
					try {
					System.out.println("processing from " + start + " to " + end);
					
					JSONObject data = null;
						System.out.println("remaining: " + ApiProxy.getCurrentEnvironment().getRemainingMillis());
						long starttime = System.currentTimeMillis();
						
						data = new JSONObject(readURL(getFitbitURL(start, end), account));
						
						System.out.println("Fetching took: " + (System.currentTimeMillis() - starttime) + "ms");
						System.out.println(data.toString());
						System.out.println("remaining: " + ApiProxy.getCurrentEnvironment().getRemainingMillis());
						if (data.has("activities-heart-intraday")) {
							System.out.println("activities-heart-intraday found");
							JSONObject intra = data.getJSONObject("activities-heart-intraday");
							JSONArray dataset = intra.getJSONArray("dataset");

							System.out.println("# datapoints: " + dataset.length());

							for (int i = 0; i < dataset.length(); i++) {
								JSONObject datapoint = dataset.getJSONObject(i);
								System.out.println("time: " + datapoint.getString("time") + " rate " + datapoint.getInt("value"));
							}

						}
						System.out.println("remaining: " + ApiProxy.getCurrentEnvironment().getRemainingMillis());

					
					// store time of last block data that was synchronized
					account.setLastSynced(end.toDate());
					OauthServiceAccountManager.updateOauthServiceAccount(account);
					
					// Daisy chain task for next hour
					if (end.plus(Duration.standardHours(1)).isBeforeNow()) {
						new FitbitTask(account.getAccountId()).scheduleTask();;
					}
					} catch (JSONException | IOException ex) {
						log.log(Level.SEVERE, "task aborted", ex);
					}
				}
			}
			
		} else {
			log.severe("no fitbit service account found for " + accountId);
		}

	}

	private OauthServiceAccount getAccount(final String accountId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(OauthServiceAccount.class);
			try {
				q.setFilter("serviceId == serviceIdParam");
				q.setFilter("accountId == accountIdParam");
				q.declareParameters("Integer serviceIdParam, String accountIdParam");

				@SuppressWarnings("unchecked")
				List<OauthServiceAccount> result = (List<OauthServiceAccount>) q.execute(AccountJDO.FITBITCLIENT,
						accountId);
				return result.isEmpty() ? null : result.get(0);
			} finally {
				q.closeAll();
			}

		} finally {
			pm.close();
		}
	}

	private URL getFitbitURL(DateTime start, DateTime end) throws MalformedURLException {
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

		return new String(baos.toByteArray());
	}

	private void exchangeCodeForAccessToken(final OauthServiceAccount account) {
		OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(account.getServiceId());
		String client_id = jdo.getClient_id();
		String client_secret = jdo.getClient_secret();

		System.out.println(client_id);
		System.out.println(client_secret);
		System.out.println(account.getRefreshToken());

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
