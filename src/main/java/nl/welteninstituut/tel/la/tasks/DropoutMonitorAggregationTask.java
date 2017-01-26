package nl.welteninstituut.tel.la.tasks;

import nl.welteninstituut.tel.la.chartobjects.DropOutObject;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
@Deprecated
public class DropoutMonitorAggregationTask extends GenericBean {

    private String queryId;
    public DropoutMonitorAggregationTask() {

    }
    public DropoutMonitorAggregationTask(String queryId) {
        this.queryId=queryId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public void run() {
        try {
            JSONObject courseLoginOverview = new JSONObject(QueryCacheManager.getQueryResult("course_login_overview"));
            JSONArray array = courseLoginOverview.getJSONArray("rows");
            DropOutObject dropOutObject = new DropOutObject();
            for (int i=0; i<array.length();i++) {
                JSONArray row = array.getJSONObject(i).getJSONArray("c");
                String uri = row.getJSONObject(0).getString("v");
                int launches =Integer.parseInt(row.getJSONObject(1).getString("v"));
                dropOutObject.setLaunches(uri, launches);
                JSONObject courseInfo = new JSONObject(QueryCacheManager.getQueryResult("course_activities_" + uri));
//                System.out.println(courseInfo);
                JSONArray arrayCourse = courseInfo.getJSONArray("rows");
                long amountOfActivities = 0l;
                for (int j=0; j<arrayCourse.length();j++) {
                    JSONArray rowCourse = arrayCourse.getJSONObject(j).getJSONArray("c");
//                    System.out.println(rowCourse);

                    String verb = rowCourse.getJSONObject(0).getString("v");
                    if (!(verb.equals("http://adlnet.gov/expapi/verbs/launched")&&
                            verb.equals("http://adlnet.gov/expapi/verbs/registered"))){
                        amountOfActivities += Integer.parseInt(rowCourse.getJSONObject(1).getString("v"));
                    }
//
                }
                dropOutObject.setActivites(uri, amountOfActivities);


//                launchMap.put(uri, Integer.parseInt(row.getJSONObject(1).getString("v")));
//                activitiesMap.put(row.getJSONObject(0).getString("v"), Long.parseLong(row.getJSONObject(1).getString("v")));
//                registeredUserMap.put(row.getJSONObject(0).getString("v"), Integer.parseInt(row.getJSONObject(1).getString("v")));



            }
            QueryCacheManager.addQueryResult(queryId, dropOutObject.toJsonObject().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
