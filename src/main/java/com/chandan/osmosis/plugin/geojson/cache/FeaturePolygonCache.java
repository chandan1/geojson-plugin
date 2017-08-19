package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.h2.mvstore.MVStore;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by chandan on 24/04/17.
 */
public class FeaturePolygonCache extends Cache<Polygon> {

	private TypeReference<Feature<Polygon>> typeReference = new TypeReference<Feature<Polygon>>() {
		@Override
		public Type getType() {
			return super.getType();
		}
	};

	public FeaturePolygonCache(String pathToDir) {
		super(pathToDir, "featurePolygonDb");
	}

	@Override
	public TypeReference getTypeReference() {
		return typeReference;
	}
}
