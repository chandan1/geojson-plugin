package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.GeoJson;
import com.chandan.geojson.model.Geometry;

public interface Cache<T extends GeoJson> {
	
	void open();
	
	T get(long key);
	
	void put(long key, T t);

	void close();
}
