package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.primitives.Longs;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;


public class FeaturePointCache implements Cache<Feature<Point>>{

	private DB pointCacheDb;
	private final String pathToDir;
	
	public FeaturePointCache(String pathToDir) {
		this.pathToDir = pathToDir;
	}

	@Override
	public void open() {
		Options options = new Options();
		options.createIfMissing(true);
		try {
			pointCacheDb = JniDBFactory.factory.open(new File(pathToDir + "/pointCacheDb"), options);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			pointCacheDb.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Feature<Point> get(long key) {

		byte[] data = pointCacheDb.get(Longs.toByteArray(key));
		if (data != null) {
			try {
				return Utils.<Feature<Point>>jsonDecode(data, new TypeReference<Feature<Point>>() {});
			} catch (Exception e) {
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
				pointCacheDb.put(Longs.toByteArray(key), data.getBytes());
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
