package nl.welteninstituut.tel.la.export;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.tasks.GenericBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public abstract class Export{


    public static Export[] getExports(){
        System.out.println(Configuration.get(Configuration.EXPORTERS));
        List<String> classAsStrings = new ArrayList<String>(Arrays.asList(Configuration.get(Configuration.EXPORTERS).split(";")));
        Export[] result = new Export[classAsStrings.size()];
        int i=0;
        for (String classAsString: classAsStrings){
            try {
                result[i++] = (Export) Class.forName(classAsString).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public abstract boolean synchronous();

    public abstract String exportMetadata(String authorization, String metadata) throws ExportException;

    public abstract String exportMetadata(String authorization, String metadata, String identifier) throws ExportException;
}
