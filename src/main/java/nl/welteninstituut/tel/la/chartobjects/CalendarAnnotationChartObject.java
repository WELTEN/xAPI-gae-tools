package nl.welteninstituut.tel.la.chartobjects;

import org.codehaus.jettison.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
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
public class CalendarAnnotationChartObject extends ChartObject {

    private HashMap<String, Integer> hm = new HashMap<String, Integer>();

    public CalendarAnnotationChartObject() {
        addCol(0, "Date", DATE);
        addCol(0, "Type", STRING);
        addCol(1, "Logins", NUMBER);
    }



    public void addRow(String date, String type, int amount) {
        hm.put(date+"*"+type, amount);
    }

    public JSONObject toJsonObject() {
        for (String dateString : hm.keySet()) {
            String hashKey = dateString;
            super.addRow(dateString.substring(0, dateString.indexOf('*')),dateString.substring(dateString.indexOf('*')+1), hm.get(dateString));
        }

        return super.toJsonObject();
    }


    public static void main(String[] args) {
        CalendarAnnotationChartObject object = new CalendarAnnotationChartObject();
        object.addRow("date", "type", 5);
        object.addRow("date2", "type", 5);
        System.out.println(object.toJsonObject());
    }
}
