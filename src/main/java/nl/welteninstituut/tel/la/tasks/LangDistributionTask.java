package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import nl.welteninstituut.tel.la.bigquery.Common;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
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
public class LangDistributionTask extends GenericBean {
    private String jobId;
    private String cacheKey;

    public LangDistributionTask() {

    }

    public LangDistributionTask(String jobId, String cacheKey) {
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
                LearnerAverageActivities learnerAverageActivities = new LearnerAverageActivities();
                String csv = "Language, amount\n";
                String result = "[";
                boolean first = true;
                if (rows != null)
                    for (TableRow row : rows) {
                        List rowList = row.getF();
                        String lang = ((TableCell) rowList.get(0)).getV() + "";
                        String amount = ((TableCell) rowList.get(1)).getV() + "";
                        if (!first) {
                            result += ",";
                        } else {
                            first = false;
                        }
                        result += "{\"label\":\"" + CourseDateToObjectDefinitionManager.keyMapper(lang) + "\",\"rate\":" + amount + "}";
                        csv += CourseDateToObjectDefinitionManager.keyMapper(lang) + "," + amount + "\n";
                    }
                result += "]";
                QueryCacheManager.addQueryResult(cacheKey, result);
                QueryCacheManager.addQueryResult(cacheKey + "_csv", csv);
            } else {
                scheduleTask();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
