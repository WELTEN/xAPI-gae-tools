package nl.welteninstituut.tel.sandbox;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
public class ImportMoocLangs {

    public static void main(String[] args) throws Exception {
        File fXmlFile = new File("/Users/str/Desktop/test.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        NodeList records = ((Element) doc.getDocumentElement().getElementsByTagName("ListRecords").item(0)).getElementsByTagName("record");
        HashMap<String, Vector<String>> hm= new HashMap<String, Vector<String>>();
        for (int i = 0; i < records.getLength(); i++) {
            Element record = ((Element) records.item(i));
            NodeList metadataNL = record.getElementsByTagName("metadata");
            if (metadataNL.getLength() == 1) {
                Element metadata = (Element) metadataNL.item(0);
                String id = ((Element) ((Element) record.getElementsByTagName("header").item(0)).getElementsByTagName("identifier").item(0)).getTextContent();
                System.out.println(id);
                NodeList languagesList = ((Element) metadata.getChildNodes().item(0)).getElementsByTagName("dc:language");
                for (int j = 0; j < languagesList.getLength(); j++) {
                    String langKey = languagesList.item(j).getTextContent();

                    if (!hm.containsKey(langKey)) {
                        hm.put(langKey, new Vector<String>());
                    }
                    hm.get(langKey).add(id);
                }
//                System.out.println(metadata.getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0/oai_dc/","dc").item(0));
            }
        }
        System.out.println(hm);
        String finalQuery = "SELECT amount, lang FROM ";
        for(String lang : hm.keySet()){

            String query = "(SELECT count(*) AS amount, \""+lang+"\" AS lang FROM [xAPIStatements.xapiTable] where ";
            for (String id: hm.get(lang)) {
                if (id.startsWith("oai:")) id = id.substring(4);
                query += "(courseId contains \""+id + "\")";
            }
            query += "),";
            finalQuery += query.replaceAll("[)][(]",") OR (");
//            System.out.println(query.replaceAll("[)][(]",") OR ("));
        }
        finalQuery = finalQuery.substring(0,finalQuery.lastIndexOf(",")) +";";
        System.out.println(finalQuery);

    }

    public static void main3(String[] langs) throws Exception{
//        File fXmlFile = new File("/Users/str/Desktop/test.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new URL("https://backend.ecolearning.eu/oai?verb=ListRecords&metadataPrefix=oai_dc").openStream());
        NodeList records = ((Element) doc.getDocumentElement().getElementsByTagName("ListRecords").item(0)).getElementsByTagName("record");
        HashMap<String, String> hm= new HashMap<String, String>();
        for (int i = 0; i < records.getLength(); i++) {
            Element record = ((Element) records.item(i));
            NodeList metadataNL = record.getElementsByTagName("metadata");
            if (metadataNL.getLength() == 1) {
                Element metadata = (Element) metadataNL.item(0);
                String id = ((Element) ((Element) record.getElementsByTagName("header").item(0)).getElementsByTagName("identifier").item(0)).getTextContent();
                NodeList languagesList = ((Element) metadata.getChildNodes().item(0)).getElementsByTagName("dc:title");
                for (int j = 0; j < languagesList.getLength(); j++) {
                    String langKey = languagesList.item(j).getTextContent();

                   hm.put(id, langKey);
                }
//                System.out.println(metadata.getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0/oai_dc/","dc").item(0));
            }
        }
        System.out.println(hm);

    }
}
