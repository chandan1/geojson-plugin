package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.GeoJson;

public interface Cache<T extends GeoJson> {

	void open();

	T get(long key);

	void put(long key, T t);

	void close();
}
