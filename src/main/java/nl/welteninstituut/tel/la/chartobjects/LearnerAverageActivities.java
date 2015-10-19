/*
 * Copyright (C) 2015 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.welteninstituut.tel.la.chartobjects;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 * 
 */
public class LearnerAverageActivities extends ChartObject {

    private HashMap<String, Integer> hm = new HashMap<String, Integer>();

    public LearnerAverageActivities() {
        addCol(0, "Learner", STRING);
        addCol(1, "amountOfActivities", NUMBER);
    }

    public LearnerAverageActivities(JSONObject json) {
        this();
        try {
            JSONArray array = json.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONArray row = array.getJSONObject(i).getJSONArray("c");
                hm.put(row.getJSONObject(0).getString("v"), Integer.parseInt(row.getJSONObject(1).getString("v")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addRow(String learner, int amount) {
        hm.put(learner, amount);
    }

    public BarChartObject getBarChartObject(String user) {
        int yourActivities = 0;
        if (hm.containsKey(user)) yourActivities = hm.get(user);
        BarChartObject barChartObject = new BarChartObject();
        int amount = hm.size();
        int total = 0;
        for (String learnerString : hm.keySet()) {
            total += hm.get(learnerString);
        }
        barChartObject.addRow("Peers vs you", (total / amount), yourActivities);
        return barChartObject;
    }

    public JSONObject toJsonObject() {
        for (String learnerString : hm.keySet()) {
            addRow(learnerString, "" + hm.get(learnerString));
        }

        return super.toJsonObject();
    }
}
