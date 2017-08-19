package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.h2.mvstore.MVStore;

import java.util.*;

public abstract class Cache<T extends Geometry> {

	private final String pathToDir;

	private final String dbFileName;

	private MVStore mvStore;

	private Map<String, byte[]> map;

	public Cache(String pathToDir, String dbFileName) {
		this.pathToDir = pathToDir;
		this.dbFileName = dbFileName;
	}

	public void open() {
		this.mvStore = new MVStore.Builder().fileName(pathToDir + "/" + dbFileName).compress().open();
		this.map = this.mvStore.openMap(dbFileName);
	}


	public Feature<T> get(long key) {
		byte[] data = this.map.get(String.valueOf(key));
		if (data != null) {
			try {
				return Utils.<Feature<T>>jsonDecode(data, getTypeReference());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void put(long key, Feature<T> t) {
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

	public Feature<T> getAndMarkDeleted(long key) {
		Feature<T> feature = this.get(key);
		Utils.markDeleted(feature);
		this.put(key, feature);
		return feature;
	}

	public Collection<Feature<T>> getValues() {
		final Collection<byte[]> delegateCollection = this.map.values();
		if (delegateCollection == null) {
			return null;
		}
		final Iterator<byte[]> delegateIterator = delegateCollection.iterator();

		return new AbstractSet<Feature<T>>() {
			@Override
			public Iterator<Feature<T>> iterator() {
				return new Iterator<Feature<T>>() {
					@Override
					public boolean hasNext() {
						return delegateIterator.hasNext();
					}

					@Override
					public Feature<T> next() {
						byte[] data = delegateIterator.next();
						if (data == null) {
							return null;
						}
						try {
							return Utils.<Feature<T>>jsonDecode(data, getTypeReference());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void remove() {
						delegateIterator.remove();
					}
				};
			}

			@Override
			public int size() {
				return delegateCollection.size();
			}
		};
	}

	public void close() {
		this.mvStore.close();
	}

	public abstract TypeReference getTypeReference();
}
