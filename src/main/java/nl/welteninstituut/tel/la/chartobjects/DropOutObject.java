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
public class DropOutObject extends ChartObject {
    private HashMap<String, Integer> launchMap = new HashMap();
    private HashMap<String, Long> activitiesMap = new HashMap();
    private HashMap<String, Integer> registeredUserMap = new HashMap();


    public DropOutObject() {
        addCol(0, "ID", STRING);
        addCol(1, "NumberActivities", NUMBER);
        addCol(2, "NumberOfLaunches", NUMBER);
        addCol(3, "MoocProvider", STRING);
        addCol(4, "NumberOfRegisteredUsers", NUMBER);
    }

    public void setLaunches(String uri, int amount) {
        launchMap.put(uri, amount);
        registeredUserMap.put(uri, amount);
    }

    public void setActivites(String uri, long amount) {
        activitiesMap.put(uri, amount);
    }

    public JSONObject toJsonObject() {
        System.out.println("launchmap" + launchMap.keySet());
        System.out.println(launchMap.keySet().size());
        for (String uri : launchMap.keySet()) {

            String provider = uri;
            if (uri.contains("telefonicalearningservices")) provider = "weMOOC";
            if (uri.contains("ecolearning")) provider = "OpenMOOC";
            if (uri.contains("polimi")) provider = "EDX";
            if (uri.contains("humance")) provider = "LogiAssist";
            if (uri.contains("uab")) provider = "iMOOC";
            System.out.println("activities " + activitiesMap.get(uri));
            System.out.println("launchMap " + launchMap.get(uri));
            System.out.println("registeredUserMap " + registeredUserMap.get(uri));
            addRow(uri, "" + activitiesMap.get(uri), "" + launchMap.get(uri), provider, "" + registeredUserMap.get(uri));

        }

        return super.toJsonObject();
    }
}
