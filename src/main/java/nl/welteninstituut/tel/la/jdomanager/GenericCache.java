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

import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

//import net.sf.jsr107cache.Cache;
//import net.sf.jsr107cache.CacheException;
//import net.sf.jsr107cache.CacheManager;

/**
 * @author Stefaan Ternier
 * 
 */
public class GenericCache {
	private static final Logger logger = Logger.getLogger(GenericCache.class.getName());
	private static GenericCache instance;

	private static Cache cache;

	protected GenericCache() {
		if (cache == null) {
			try {
				cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
			} catch (CacheException e) {
				logger.severe(e.getMessage());
			}
		}
	}

	public static GenericCache getInstance() {
		if (instance == null)
			instance = new GenericCache();
		return instance;

	}

	protected Cache getCache() {
		return cache;
	}

	public static String generateCacheKey(java.lang.Object... parameters) {
		return generateCacheKeyAr(parameters);
	}

	private static String generateCacheKeyAr(Object parameters[]) {
		String key = "";
		for (Object p : parameters) {
			if (p == null) {
				key += ":null";
			} else {
				if (p instanceof Object[]) {
					key += generateCacheKeyAr((Object[]) p);
				} else {
					key += ":" + p.toString();
				}

			}
		}
		return key;
	}

	@SuppressWarnings("unchecked")
	protected void storeCacheKey(Long id, String prefix, String cachekey) {
		HashSet<String> hs = getCacheKey(id, prefix);
		if (hs == null) {
			hs = new HashSet<String>();
		}
		if (!hs.contains(cachekey)) {
			hs.add(cachekey);
			getCache().put(prefix + id, hs);
		}
	}

	@SuppressWarnings("unchecked")
	protected HashSet<String> getCacheKey(Long id, String prefix) {
		return (HashSet<String>) getCache().get(prefix + id);
	}

	protected boolean cacheKeyExists(Long id, String prefix, String cacheKey) {
		HashSet<String> hs = getCacheKey(id, prefix);
		if (hs == null)
			return false;
		return hs.contains(cacheKey);
	}

	protected void removeKeysForGame(Long id, String prefix) {
		getCache().remove(prefix + id);
	}

}
