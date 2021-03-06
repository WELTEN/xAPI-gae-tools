package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.bigquery.Common;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
import nl.welteninstituut.tel.util.StringPool;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.List;

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
public class ProgressQT extends GenericBean {

    private String jobId;
    private String userId;

    public ProgressQT() {

    }

    public ProgressQT(String jobId, String userId, String courseId) {
        this.jobId = jobId;
        if (userId == null) {
            this.userId = "_" + courseId;
        } else {
            this.userId = "_" + courseId + "_" + userId;
            ;
        }
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        Job pollJob = null;
        try {
            pollJob = QueryAPI.getInstance().getJob(jobId);
            if (pollJob.getStatus().getState().equals(Common.DONE)) {
                GetQueryResultsResponse queryResult = QueryAPI.getInstance().getQueryResultsResponse(jobId);
                List<TableRow> rows = queryResult.getRows();
                JSONArray array = new JSONArray();
                if (rows != null)
                    for (TableRow row : rows) {
                        List rowList = row.getF();
                        JSONObject object = new JSONObject();
                        try {
                            object.put("objectId", ((TableCell) rowList.get(0)).getV() + "");
                            object.put("count", ((TableCell) rowList.get(1)).getV());
                            object.put("date", ((TableCell) rowList.get(2)).getV() + "");
                        } catch (JSONException e) {


                        }
                        array.put(object);
                    }
                System.out.println(array.toString());
                QueryCacheManager.addQueryResult("progress" + userId, array.toString());
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}