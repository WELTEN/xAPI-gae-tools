package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.bigquery.Common;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class TimeLineQT extends GenericBean {
    private String jobId;
    private String cacheKey;

    public TimeLineQT() {

    }

    public TimeLineQT(String jobId, String cacheKey) {
        this.jobId = jobId;
        this.cacheKey = cacheKey;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Override
    public void run() {
        Job pollJob = null;
        try {
            pollJob = QueryAPI.getInstance().getJob(jobId);
            if (pollJob.getStatus().getState().equals(Common.DONE)) {
                GetQueryResultsResponse queryResult = QueryAPI.getInstance().getQueryResultsResponse(jobId);
                List<TableRow> rows = queryResult.getRows();

//                ActivitySortColumnChartObject resultObject = new ActivitySortColumnChartObject();
                HashMap<String, Vector<String>> resultMap = new HashMap<>();
                int index = 0;
                JSONObject resultJson = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    resultJson.put("results", array);
                    if (rows != null)
                        for (TableRow row : rows) {
                            List rowList = row.getF();
                            String verbId = ((TableCell) rowList.get(0)).getV() + "";
                            String objectDefinition = ((TableCell) rowList.get(1)).getV() + "";
                            String courseId = ((TableCell) rowList.get(2)).getV() + "";
                            String objectDefinitionName = ((TableCell) rowList.get(3)).getV() + "";
                            String objectDescription = ((TableCell) rowList.get(4)).getV() + "";
                            Object timeStamp = ((TableCell) rowList.get(5)).getV() ;
                            String objectid = ((TableCell) rowList.get(6)).getV() + "";
                            String origin = ((TableCell) rowList.get(7)).getV() + "";
                            String objectAccountId = ((TableCell) rowList.get(8)).getV() + "";
                            String activityId= ((TableCell) rowList.get(9)).getV() + "";

                            JSONObject activityEntry = new JSONObject();
                            activityEntry.put("verbId", verbId);
                            activityEntry.put("objectDefinition", objectDefinition);
                            activityEntry.put("courseId", courseId);
                            activityEntry.put("objectDefinitionName", objectDefinitionName);
                            activityEntry.put("objectDescription", objectDescription);
                            activityEntry.put("timeStamp", timeStamp);
                            activityEntry.put("objectid", objectid);
                            activityEntry.put("origin", origin);
                            activityEntry.put("objectAccountId", objectAccountId);
                            activityEntry.put("activityId", activityId);

                            array.put(activityEntry);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                QueryCacheManager.addQueryResult(cacheKey, resultJson.toString());
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}