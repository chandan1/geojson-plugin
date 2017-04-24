package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.h2.mvstore.MVStore;

import java.util.Map;

/**
 * Created by chandan on 24/04/17.
 */
public class FeaturePolygonCache implements Cache<Feature<Polygon>> {

	private final String pathToDir;

	private MVStore mvStore;

	private Map<String, byte[]> map;

	public FeaturePolygonCache(String pathToDir) {
		this.pathToDir = pathToDir;
	}

	@Override
	public void open() {
		this.mvStore = new MVStore.Builder().fileName(pathToDir + "/featurePolygonCacheDb").compress().open();
		this.map = this.mvStore.openMap("featureLinestringCacheDb");
	}

	@Override
	public Feature<Polygon> get(long key) {
		byte[] data = this.map.get(String.valueOf(key));
		if (data != null) {
			try {
				return Utils.<Feature<Polygon>>jsonDecode(data, new TypeReference<Feature<Polygon>>() {
				});
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;

	}

	@Override
	public void put(long key, Feature<Polygon> polygonFeature) {
		try {
			String data = Utils.jsonEncode(polygonFeature);
			if (data != null) {
				this.map.put(String.valueOf(key), data.getBytes());
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		this.mvStore.close();
	}
}
