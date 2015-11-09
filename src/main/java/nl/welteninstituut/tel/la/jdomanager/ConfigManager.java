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
package nl.welteninstituut.tel.la.jdomanager;

import javax.jdo.PersistenceManager;

import nl.welteninstituut.tel.la.jdo.Config;

/**
 * @author Stefaan Ternier
 * 
 */
public class ConfigManager {

    public static String addKey(String key, String value) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Config gi = new Config();
        gi.setKey(key);
        gi.setValue(value);
        try {
            pm.makePersistent(gi);
            return gi.getKey();
        } finally {
            pm.close();
        }
    }

//    public static String getValue(String key) {
//        PersistenceManager pm = PMF.get().getPersistenceManager();
//        String value = (String) GenericCache.getInstance().getCache().get(key);
//        if (value == null) {
//            try {
//                value = pm.getObjectById(Config.class,  key).getValue();
//                GenericCache.getInstance().getCache().put(key, value);
//
//            } finally {
//                pm.close();
//            }
//        }
//        return value;
//    }
}
