package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.bigquery.Common;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
import nl.welteninstituut.tel.la.chartobjects.ActivitySortColumnChartObject;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.chartobjects.StudentPath;
import nl.welteninstituut.tel.la.jdomanager.CourseDateToObjectDefinitionManager;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;

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
public class StudentPathsQT extends GenericBean {
    private String jobId;
    private String cacheKey;

    public StudentPathsQT() {

    }
    public StudentPathsQT(String jobId, String cacheKey) {
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
                System.out.println("amount of rows "+rows.size());
                StudentPath resultObject = new StudentPath();
                int index = 0;
                String lastActorId= null;
                String actorId = null;
                if (rows !=null)
                    for (TableRow row : rows) {
                        List rowList = row.getF();
                        actorId = ((TableCell) rowList.get(0)).getV()+"";

                        if (lastActorId !=null && !lastActorId.equals(actorId)){
                            System.out.println("new ActorId!");
                            QueryCacheManager.addQueryResult(cacheKey+"_"+index++, resultObject.toJsonObject().toString());
                            QueryCacheManager.addQueryResult(cacheKey+"_"+lastActorId, resultObject.toJsonObject().toString());
                            System.out.println("writing "+ cacheKey+"_"+lastActorId);
                            resultObject = new StudentPath();
                        }

                        String objectId = ((TableCell) rowList.get(1)).getV()+"";
                        String objectDefinition = ((TableCell) rowList.get(2)).getV()+"";
                        String verbId = ((TableCell) rowList.get(3)).getV()+"";
                        int relativeTime = (int) Double.parseDouble(((TableCell) rowList.get(4)).getV()+"");
                        resultObject.addRow(objectId, objectDefinition,verbId, relativeTime);


                        lastActorId = actorId;
                    }
                QueryCacheManager.addQueryResult(cacheKey+"_"+index++, resultObject.toJsonObject().toString());
                QueryCacheManager.addQueryResult(cacheKey+"_"+actorId, resultObject.toJsonObject().toString());
                System.out.println("writing "+ cacheKey+"_"+actorId);


            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}