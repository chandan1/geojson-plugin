package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * Created by chandan on 5/4/16.
 */
public class OsmWayToFeaturePolygonConverter implements OsmToFeatureConverter<Way, Polygon> {

	private final FeaturePointCache pointCache;

	private final FeatureLinestringCache lineStringCache;

	public OsmWayToFeaturePolygonConverter(FeaturePointCache pointCache, FeatureLinestringCache lineStringCache) {
		this.pointCache = pointCache;
		this.lineStringCache = lineStringCache;
	}

	@Override
	public Feature<Polygon> convert(Way t) {
		return null;
	}

	@Override
	public void setNext(OsmToFeatureConverter<Way, Polygon> nextConverter) {

	}

	@Override
	public void setProperties(Way way, Feature.FeatureBuilder<Polygon> propertyBuilder) {

	}
}
