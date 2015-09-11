package nl.welteninstituut.tel.la.chartobjects;

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
public class ChartObject {

    public static final int DATE = 1;
    public static final int NUMBER = 2;
    public static final int STRING = 3;

    protected JSONArray cols = new JSONArray();
    protected JSONArray rows = new JSONArray();

    public ChartObject() {
    }

    public void addCol(int i, String label, int type) {
        JSONObject col = new JSONObject();
        try {
            col.put("id", label);
            col.put("label", label);
            switch (type) {
                case DATE:
                    col.put("type", "date");
                    break;
                case NUMBER:
                    col.put("type", "number");
                    break;
                case STRING:
                    col.put("type", "string");
                    break;
            }
            cols.put(i, col);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addRow(String... row) {
        try {
            JSONObject rowObject = new JSONObject();
            JSONArray cArray = new JSONArray();
            rowObject.put("c", cArray);
            for (String r : row) {
                JSONObject rObject = new JSONObject();
                rObject.put("v", r);
                cArray.put(rObject);
            }
//            System.out.println(rowObject.toString());
            rows.put(rowObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        try {
            result.put("cols", cols);
            result.put("rows", rows);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        ChartObject object = new ChartObject();
        object.addCol(0, "Date", DATE);
        object.addCol(1, "Logins", NUMBER);
        object.addRow("Date(2012, 3, 13)", "37032");
        object.addRow("Date(2012, 3, 14)", "20");
        object.addRow("Date(2012, 3, 16)", "20");
        System.out.println(object.toJsonObject().toString());

        CalendarObject calendarObject = new CalendarObject();
        calendarObject.addRow(System.currentTimeMillis());
        calendarObject.addRow(System.currentTimeMillis() - (24 * 60 * 60 * 1000 * 3));
        calendarObject.addRow(System.currentTimeMillis());

        System.out.println(calendarObject.toJsonObject().toString());


        LearnerAverageActivities learnerAverageActivities = new LearnerAverageActivities();
        learnerAverageActivities.addRow("stefaan", 30);
        learnerAverageActivities.addRow("hendrick", 20);
        learnerAverageActivities.addRow("maren", 10);
        System.out.println(learnerAverageActivities.getBarChartObject("stefaan").toJsonObject().toString());

        DropOutObject dropOutObject = new DropOutObject();
        dropOutObject.setActivites("eu.ecolearning.hub0", 2);
        dropOutObject.setLaunches("eu.ecolearning.hub0", 20);
        dropOutObject.setActivites("eu.ecolearning.hub1", 4);
        dropOutObject.setLaunches("eu.ecolearning.hub1", 10);
        dropOutObject.setActivites("eu.ecolearning.hub0", 2);
        dropOutObject.setLaunches("eu.ecolearning.hub0", 20);
        System.out.println(dropOutObject.toJsonObject().toString());
    }
}
