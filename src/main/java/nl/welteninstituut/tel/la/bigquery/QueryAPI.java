package nl.welteninstituut.tel.la.bigquery;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationQuery;
import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.rest.BigQuery;

import java.io.IOException;

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
public class QueryAPI {

    private static QueryAPI instance;

    private QueryAPI(){

    }

    public static QueryAPI getInstance(){
        if (instance ==  null) instance = new QueryAPI();
        return instance;
    }

    public String createQueryJob(String query){
        try {
            Job job = new Job();
            JobConfiguration config = new JobConfiguration();
            JobConfigurationQuery queryConfig = new JobConfigurationQuery();
            config.setQuery(queryConfig);

            job.setConfiguration(config);
            queryConfig.setQuery(query);
            Bigquery.Jobs.Insert insert = Common.bigquery.jobs().insert(Configuration.get(Configuration.BQProject), job);
            insert.setProjectId(Configuration.get(Configuration.BQProject));
            return insert.execute().getJobReference().getJobId();
        } catch (IOException e) {

        }
        return null;
    }

    public Job getJob(String jobId) throws IOException {
        return Common.bigquery.jobs().get(Configuration.get(Configuration.BQProject), jobId).execute();
    }

    public GetQueryResultsResponse getQueryResultsResponse(String jobId) throws IOException {
        return Common.bigquery.jobs().getQueryResults(Configuration.get(Configuration.BQProject),
                jobId).execute();
    }
}
