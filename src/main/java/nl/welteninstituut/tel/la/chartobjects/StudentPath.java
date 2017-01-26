package nl.welteninstituut.tel.la.chartobjects;

import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;

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
public class StudentPath extends ChartObject{
    private HashMap<String, Integer> peersMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> youMap = new HashMap<String, Integer>();

    public StudentPath() {
        addCol(0, "ObjectId", STRING);
        addCol(0, "ObjectDefinition", STRING);
        addCol(0, "verbId", STRING);
        addCol(1, "RelativeTime ", NUMBER);
    }

    public void addRow(String title, int peers, int you) {
        peersMap.put(title, peers);
        youMap.put(title, you);
    }

    public JSONObject toJsonObject() {
        for (String learnerString : peersMap.keySet()) {
            addRow("" + peersMap.get(learnerString), "" + youMap.get(learnerString));
        }
        return super.toJsonObject();
    }

    public static void main(String[] args) {
        StudentPath o = new StudentPath();
        o.addRow("https://hub0.ecolearning.eu/course/smooc-step-by-step-3ed/classroom/#unit75/kq175", "video","verbId", 0);
        o.addRow("https://hub0.ecolearning.eu/course/smooc-step-by-step-3ed/classroom/#unit75/kq175", "video","verbId", 0);
        System.out.println(o.toJsonObject());
    }
}
