package nl.welteninstituut.tel.sandbox;

import java.io.*;
import java.util.*;

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
public class CSVEmailIDs {
//    private static String courseId="8.20";
//    private static String courseId="8.57";
//    private static String courseId="8.59";
//    private static String courseId="10b.13";
    private static String courseId="11.20";

    private static HashSet<String> hashMap = new HashSet<>();
    private static HashMap<String, String> emailMapping = new HashMap<>();
    private static HashMap<String, String> nameMapping = new HashMap<>();

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/Users/str/Documents/eco/"+courseId+".csv"));
        String         line = null;
        reader.readLine();
            while((line = reader.readLine()) != null) {
//                System.out.println(line);
                hashMap.add(line);
            }

//        System.out.println(hashMap);


        BufferedReader userreader = new BufferedReader(new FileReader("/Users/str/Documents/eco/usersLA.csv"));



//        readAllEcoUsers();
        readAllEcoUsersNew();

        PrintWriter writer = new PrintWriter("/Users/str/Documents/eco/"+courseId+"-filtered.csv", "UTF-8");
        Iterator<String> it = hashMap.iterator();
        while (it.hasNext()) {
            String next = it.next();

            if (emailMapping.get(next)!=null){
                writer.println(emailMapping.get(next)+ ","+nameMapping.get(next));
                System.out.println(emailMapping.get(next)+ ","+nameMapping.get(next));
            }

        }
        writer.close();

    }

    public static String removeQuotes(String input) {
        if (input.contains("\"")){
            return input.substring(1, input.length()-1);
        }
        return input;
    }

    public static void readAllEcoUsers() throws Exception{
        BufferedReader userreader = new BufferedReader(new FileReader("/Users/str/Documents/eco/usersLA.csv"));

        String         line = null;

        int i = 0;
        userreader.readLine();
        while((line = userreader.readLine()) != null &&i <10) { //&&i <10
            i++;
            String lineArray[] = line.split(",", -1);
            String id = lineArray[0];
            id = id.substring(id.indexOf('(')+1, id.indexOf(')'));
            String email = removeQuotes(lineArray[1]);
            String given = removeQuotes(lineArray[2]);
            String middle = removeQuotes(lineArray[3]);
            String family = removeQuotes(lineArray[4]);
            System.out.println(id + " "+email+ "-" + given + "-" + middle + "-" + family);
            emailMapping.put(id, email);
            if (given.length() == 0 && middle.length() ==0 && family.length() ==0) {
                given = email;
            }
            nameMapping.put(id, given+","+middle+ ","+family);

        }
    }

    public static void readAllEcoUsersNew()throws Exception{
        BufferedReader userreader = new BufferedReader(new FileReader("/Users/str/Documents/eco/usersLAnew.csv"));

        String         line = null;

        int i = 0;
        userreader.readLine();
        while((line = userreader.readLine()) != null ) { //&&i <10
            i++;
            List<String> lineList = parseLine(line, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
//            System.out.println(test +" "+test.size());
            String lineArray[] = line.split(",",-1);
//            System.out.println(lineArray[0] + " "+lineArray[18]  + " "+lineArray[19] + " "+lineArray.length);
            String id = lineList.get(19);
            id = id.substring(id.indexOf('(')+1, id.indexOf(')'));
            String email = removeQuotes(lineList.get(0));
            String given = removeQuotes(lineList.get(2));
            String middle = removeQuotes(lineList.get(3));
            String family = removeQuotes(lineList.get(4));
            System.out.println(id + " "+email+ "-" + given + "-" + middle + "-" + family);
            emailMapping.put(id, email);
            if (given.length() == 0 && middle.length() ==0 && family.length() ==0) {
                given = email;
            }
            nameMapping.put(id, given+","+middle+ ","+family);

        }

    }
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}
