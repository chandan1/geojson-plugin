package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.h2.mvstore.MVStore;

import java.util.Map;

public class FeaturePointCache implements Cache<Feature<Point>> {

	private final String pathToDir;

	private MVStore mvStore;

	private Map<String, byte[]> map;

	public FeaturePointCache(String pathToDir) {
		this.pathToDir = pathToDir;
	}

	@Override
	public void open() {
		this.mvStore = new MVStore.Builder().fileName(pathToDir + "/pointCacheDb").compress().open();
		this.map = this.mvStore.openMap("pointCacheDb");
	}

	@Override
	public void close() {
		this.mvStore.close();
	}

	@Override
	public Feature<Point> get(long key) {

		byte[] data = this.map.get(String.valueOf(key));
		if (data != null) {
			try {
				return Utils.<Feature<Point>>jsonDecode(data, new TypeReference<Feature<Point>>() {});
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public void put(long key, Feature<Point> t) {
		try {
			String data = Utils.jsonEncode(t);
			if (data != null) {
				this.map.put(String.valueOf(key), data.getBytes());
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
