package nl.welteninstituut.tel.la.importers;

import nl.welteninstituut.tel.la.Configuration;

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
public abstract class Importer {

    public static Importer[] getImporters(){
        System.out.println(Configuration.get(Configuration.IMPORTERS));
        if (Configuration.get(Configuration.IMPORTERS) == null) {
            return new Importer[0];
        }
        List<String> classAsStrings = new ArrayList<String>(Arrays.asList(Configuration.get(Configuration.IMPORTERS).split(";")));
        Importer[] result = new Importer[classAsStrings.size()];
        int i=0;
        for (String classAsString: classAsStrings){
            try {
                result[i++] = (Importer) Class.forName(classAsString).newInstance();
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

    public abstract void startImport();
}
