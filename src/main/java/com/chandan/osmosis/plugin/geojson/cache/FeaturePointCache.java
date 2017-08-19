package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

public class FeaturePointCache extends Cache<Point> {

	private TypeReference<Feature<Point>> typeReference = new TypeReference<Feature<Point>>() {
		@Override
		public Type getType() {
			return super.getType();
		}
	};

	public FeaturePointCache(String pathToDir) {
		super(pathToDir, "featurePointDb");
	}

	@Override
	public TypeReference getTypeReference() {
		return typeReference;
	}
}
