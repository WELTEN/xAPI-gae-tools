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
public class SocialFollowsQT extends GenericBean {
    private String jobId;
    private String cacheKey;

    public SocialFollowsQT() {

    }

    public SocialFollowsQT(String jobId, String cacheKey) {
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
            String csv = "User,UserThatIsFollowed\n";
            if (pollJob.getStatus().getState().equals(Common.DONE)) {
                GetQueryResultsResponse queryResult = QueryAPI.getInstance().getQueryResultsResponse(jobId);
                List<TableRow> rows = queryResult.getRows();

//                ActivitySortColumnChartObject resultObject = new ActivitySortColumnChartObject();
                HashMap<String, Vector<String>> resultMap = new HashMap<>();
                int index = 0;
                if (rows != null)
                    for (TableRow row : rows) {
                        List rowList = row.getF();
                        String actorId = ((TableCell) rowList.get(0)).getV() + "";
                        String targetId = ((TableCell) rowList.get(1)).getV() + "";
                        if (!resultMap.containsKey(actorId)) resultMap.put(actorId, new Vector<String>());
                        if (!resultMap.containsKey(targetId)) resultMap.put(targetId, new Vector<String>());
                        resultMap.get(actorId).add(targetId);
                    }
                JSONObject resultJson = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    resultJson.put("followers", array);

                    for (String actorId : resultMap.keySet()) {
                        JSONObject actorObject = new JSONObject();
                        actorObject.put("id", actorId);
                        actorObject.put("name", actorId);
                        actorObject.put("image", "http://mypic.nl/00001");
                        JSONArray arrayFollows = new JSONArray();
                        for (String targetActor: resultMap.get(actorId)){
                            JSONObject targetobject = new JSONObject();
                            targetobject.put("id", targetActor);
                            csv += actorId+","+targetActor+"\n";
                            arrayFollows.put(targetobject);

                        }
                        actorObject.put("follows", arrayFollows);
                        array.put(actorObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                QueryCacheManager.addQueryResult(cacheKey, resultJson.toString());
                QueryCacheManager.addQueryResult(cacheKey+"_csv", csv);
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
