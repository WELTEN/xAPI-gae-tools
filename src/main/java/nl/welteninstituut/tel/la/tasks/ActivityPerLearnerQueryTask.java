package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.chartobjects.CalendarObject;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
import nl.welteninstituut.tel.la.rest.BigQuery;

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
public class ActivityPerLearnerQueryTask extends GenericBean {

    private String jobId;
    private String courseId;

    public ActivityPerLearnerQueryTask(){

    }

    public ActivityPerLearnerQueryTask(String jobId, String courseId) {
        this.jobId = jobId;
        this.courseId = courseId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public void run() {
        Job pollJob = null;
        try {
            pollJob = BigQuery.getBigQuery().jobs().get(Configuration.get(Configuration.BQProject), jobId).execute();
            if (pollJob.getStatus().getState().equals("DONE")) {
                GetQueryResultsResponse queryResult = BigQuery.getBigQuery().jobs().getQueryResults(Configuration.get(Configuration.BQProject),
                        jobId).execute();
                List<TableRow> rows = queryResult.getRows();
                LearnerAverageActivities learnerAverageActivities = new LearnerAverageActivities();
                if (rows !=null)
                for (TableRow row : rows) {
                    List rowList = row.getF();
                    String actorId = ((TableCell) rowList.get(0)).getV()+"";
                    String count = ((TableCell) rowList.get(1)).getV()+"";
                    learnerAverageActivities.addRow(actorId, count);
                }

                QueryCacheManager.addQueryResult("activity_per_learner_" + courseId, learnerAverageActivities.toJsonObject().toString());
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
