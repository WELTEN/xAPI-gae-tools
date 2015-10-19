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

import org.codehaus.jettison.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 * 
 */
public class CalendarObject extends ChartObject {

    private HashMap<String, Integer> hm = new HashMap<String, Integer>();

    public CalendarObject() {
        addCol(0, "Date", DATE);
        addCol(1, "Logins", NUMBER);
    }

    public void addRow(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        if (calendar.get(Calendar.YEAR) > 2010 && calendar.get(Calendar.YEAR) < 2020) {
            String dateString = "Date(" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + ")";
            if (hm.containsKey(dateString)) {
                hm.put(dateString, hm.get(dateString) + 1);
            } else {
                hm.put(dateString, 1);
            }
        }
    }

    public JSONObject toJsonObject() {
        for (String dateString : hm.keySet()) {
            addRow(dateString, "" + hm.get(dateString));
        }

        return super.toJsonObject();
    }


}
