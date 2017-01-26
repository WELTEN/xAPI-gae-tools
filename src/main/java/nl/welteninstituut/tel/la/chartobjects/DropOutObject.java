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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 * 
 */
public class DropOutObject extends ChartObject {
    private HashMap<String, Integer> launchMap = new HashMap<String, Integer>();
    private HashMap<String, Long> activitiesMap = new HashMap<String, Long>();
    private HashMap<String, Integer> registeredUserMap = new HashMap<String, Integer>();


    private static HashMap<String, String> courseIdsToLang =  new HashMap<String, String>();
    static {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new URL("https://backend.ecolearning.eu/oai?verb=ListRecords&metadataPrefix=oai_dc").openStream());
            NodeList records = ((Element) doc.getDocumentElement().getElementsByTagName("ListRecords").item(0)).getElementsByTagName("record");
            for (int i = 0; i < records.getLength(); i++) {
                Element record = ((Element) records.item(i));
                NodeList metadataNL = record.getElementsByTagName("metadata");
                if (metadataNL.getLength() == 1) {
                    Element metadata = (Element) metadataNL.item(0);
                    String id = ((Element) ((Element) record.getElementsByTagName("header").item(0)).getElementsByTagName("identifier").item(0)).getTextContent();
                    NodeList languagesList = ((Element) metadata.getChildNodes().item(0)).getElementsByTagName("dc:title");
                    for (int j = 0; j < languagesList.getLength(); j++) {
                        String langKey = languagesList.item(j).getTextContent();

                        courseIdsToLang.put(id, langKey);
                    }
//                System.out.println(metadata.getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0/oai_dc/","dc").item(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public DropOutObject() {
        addCol(0, "ID", STRING);
        addCol(1, "NumberActivities", NUMBER);
        addCol(2, "NumberOfLaunches", NUMBER);
        addCol(3, "MoocProvider", STRING);
        addCol(4, "NumberOfRegisteredUsers", NUMBER);
    }

    public void setLaunches(String uri, int amount) {
        launchMap.put(uri, amount);
   }

    public void setUsers(String uri, int amount) {
        registeredUserMap.put(uri, amount);
    }

    public void setActivites(String uri, long amount) {
        activitiesMap.put(uri, amount);
    }

    public JSONObject toJsonObject() {
        rows = new JSONArray();
        for (String uri : launchMap.keySet()) {

            String provider = uri;
            if (uri.contains("telefonicalearningservices")) provider = "weMOOC";
            if (uri.contains("ecolearning")) provider = "OpenMOOC";
            if (uri.contains("polimi")) provider = "EDX";
            if (uri.contains("humance")) provider = "LogiAssist";
            if (uri.contains("uab")) provider = "iMOOC";

            addRow(uri, "" + activitiesMap.get(uri), "" + launchMap.get(uri), provider, "" + registeredUserMap.get(uri));
        }

        return super.toJsonObject();
    }

    public JSONObject toJsonObjectLang() {
        rows = new JSONArray();
        addCol(0, "ID", STRING);
        addCol(1, "NumberOfRegisteredUsers", NUMBER);
        addCol(2, "NumberOfLaunches", NUMBER);
        addCol(3, "Lang", STRING);
        addCol(4, "NumberActivities", NUMBER);

        String de = "{de=[oai:de.humance.education:80286, oai:de.humance.education:50505, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub11:7]";
        String it= "[oai:it.polimi.pok:course-v1:Polimi+MAT101+2016_M8, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub11:7, oai:eu.ecolearning.hub10b:13]";
        String pt= "[oai:eu.ecolearning.hub4:12, oai:pt.uab.imooc.eco:course_15_e-skills-E, oai:eu.ecolearning.hub11:22, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub4:14, oai:pt.uab.imooc.eco:course_14_eteach002, oai:pt.uab.imooc.eco:course_13_eteach001, oai:eu.ecolearning.hub11:7]";
        String fr= "[oai:eu.ecolearning.hub5:2, oai:eu.ecolearning.hub5:1, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub11:27, oai:eu.ecolearning.hub11:7, oai:eu.ecolearning.hub11:24, oai:eu.ecolearning.hub11:8]";
        String en= "[oai:it.polimi.pok:course-v1:Polimi+FC101+2016_M9, oai:eu.ecolearning.hub5:1, oai:it.polimi.pok:course-v1:Polimi+WMT101+2016_M9, oai:eu.ecolearning.hub8:58, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub2:12, oai:eu.ecolearning.hub11:7, oai:eu.ecolearning.hub11:65]";
        String es= "[oai:eu.ecolearning.hub8:7, oai:eu.ecolearning.hub8:8, oai:eu.ecolearning.hub8:10, oai:eu.ecolearning.hub8:20, oai:eu.ecolearning.hub8:59, oai:eu.ecolearning.hub11:3, oai:eu.ecolearning.hub11:9, oai:eu.ecolearning.hub11:14, oai:eu.ecolearning.hub8:57, oai:eu.ecolearning.hub8:62, oai:eu.ecolearning.hub11:1, oai:eu.ecolearning.hub11:21, oai:eu.ecolearning.hub8:9, oai:eu.ecolearning.hub0:11, oai:eu.ecolearning.hub8:61, oai:eu.ecolearning.hub11:25, oai:eu.ecolearning.hub11:5, oai:eu.ecolearning.hub11:12, oai:eu.ecolearning.hub11:34, oai:eu.ecolearning.hub11:36, oai:eu.ecolearning.hub11:37, oai:eu.ecolearning.hub11:32, oai:eu.ecolearning.hub11:7, oai:eu.ecolearning.hub1:27, oai:eu.ecolearning.hub11:35, oai:eu.ecolearning.hub11:38, oai:eu.ecolearning.hub11:39, oai:eu.ecolearning.hub7:8, oai:eu.ecolearning.hub11:54, oai:eu.ecolearning.hub11:45, oai:eu.ecolearning.hub11:53, oai:eu.ecolearning.hub11:48, oai:eu.ecolearning.hub11:50, oai:eu.ecolearning.hub11:47, oai:eu.ecolearning.hub11:49, oai:eu.ecolearning.hub11:41, oai:eu.ecolearning.hub11:43, oai:eu.ecolearning.hub11:59, oai:eu.ecolearning.hub11:63, oai:eu.ecolearning.hub11:61, oai:eu.ecolearning.hub11:62, oai:eu.ecolearning.hub11:55, oai:eu.ecolearning.hub11:60, oai:eu.ecolearning.hub11:67, oai:eu.ecolearning.hub11:56, oai:eu.ecolearning.hub11:18, oai:eu.ecolearning.hub11:57, oai:eu.ecolearning.hub11:64, oai:eu.ecolearning.hub11:65, oai:eu.ecolearning.hub11:51, oai:eu.ecolearning.hub11:58]}";
        for (String uri : launchMap.keySet()) {
            String provider = "es";
            if (de.contains(uri)) provider = "de";
            if (it.contains(uri)) provider = "it";
            if (pt.contains(uri)) provider = "pt";
            if (fr.contains(uri)) provider = "fr";
            if (en.contains(uri)) provider = "en";
            if (es.contains(uri)) provider = "es";
            String key = uri;
            if (!key.startsWith("oai")) key = "oai:"+uri;
            addRow(courseIdsToLang.get(key), "" + registeredUserMap.get(uri), "" + launchMap.get(uri), provider, "" + activitiesMap.get(uri));

        }

        try {
            ;
            return new JSONObject(super.toJsonObject().toString().replace("MoocProvider", "Language"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toJsonObject();
    }
}
