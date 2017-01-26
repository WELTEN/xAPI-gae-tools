package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.bigquery.Common;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
import nl.welteninstituut.tel.la.chartobjects.DropOutObject;
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
public class DropoutMonitorTask extends GenericBean {

    private String queryId;
    private String jobId;

    public DropoutMonitorTask() {

    }
    public DropoutMonitorTask(String jobId, String queryId) {
        this.queryId=queryId;
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public void run() {
        Job pollJob = null;
        try {
            System.out.println(jobId);
            pollJob = QueryAPI.getInstance().getJob(jobId);
            if (pollJob.getStatus().getState().equals(Common.DONE)) {
                GetQueryResultsResponse queryResult = QueryAPI.getInstance().getQueryResultsResponse(jobId);
                List<TableRow> rows = queryResult.getRows();
                DropOutObject dropOutObject = new DropOutObject();
                if (rows != null)
                    for (TableRow row : rows) {
                        List rowList = row.getF();
                        String courseId = ((TableCell) rowList.get(0)).getV()+"";
                        String statements = ((TableCell) rowList.get(1)).getV()+"";
                        String launches = ((TableCell) rowList.get(2)).getV()+"";
                        String users = ((TableCell) rowList.get(3)).getV()+"";
                        dropOutObject.setActivites(courseId, Integer.parseInt(statements));
                        dropOutObject.setLaunches(courseId, Integer.parseInt(launches));
                        dropOutObject.setUsers(courseId, Integer.parseInt(users));
                    }
                QueryCacheManager.addQueryResult( queryId, dropOutObject.toJsonObject().toString());
                QueryCacheManager.addQueryResult( queryId+"_lang", dropOutObject.toJsonObjectLang().toString());
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            System.out.println("exception "+e.getMessage());
            e.printStackTrace();
        }

    }

}
