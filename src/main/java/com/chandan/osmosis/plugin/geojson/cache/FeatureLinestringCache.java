package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

public class FeatureLinestringCache extends Cache<LineString> {

	private TypeReference<Feature<LineString>> typeReference = new TypeReference<Feature<LineString>>() {
		@Override
		public Type getType() {
			return super.getType();
		}
	};

	public FeatureLinestringCache(String pathToDir) {
		super(pathToDir, "featureLinestringDb");
	}

	@Override
	public TypeReference getTypeReference() {
		return typeReference;
	}

}
