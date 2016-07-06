package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.primitives.Longs;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;

public class FeatureLinestringCache implements Cache<Feature<LineString>> {

	private final String pathToDir;

	private DB featureLinestringCacheDb;

	public FeatureLinestringCache(String pathToDir) {
		this.pathToDir = pathToDir;
	}

	@Override
	public void open() {
		Options options = new Options();
		options.createIfMissing(true);
		try {
			featureLinestringCacheDb = JniDBFactory.factory
					.open(new File(pathToDir + "/featureLinestringCacheDb"), options);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			featureLinestringCacheDb.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Feature<LineString> get(long key) {
		byte[] data = featureLinestringCacheDb.get(Longs.toByteArray(key));
		if (data != null) {
			try {
				return Utils.<Feature<LineString>>jsonDecode(data, new TypeReference<Feature<LineString>>() {
				});
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public void put(long key, Feature<LineString> t) {

		try {
			String data = Utils.jsonEncode(t);
			if (data != null) {
				featureLinestringCacheDb.put(Longs.toByteArray(key), data.getBytes());
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
