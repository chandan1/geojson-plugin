package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Coordinate;
import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;

import java.util.HashMap;
import java.util.Map;

public class OsmNodeToFeaturePointConverter implements OsmToFeatureConverter<Node, Point> {

	private final FeaturePointCache featurePointCache;

	private OsmToFeatureConverter<Node, Point> nextConverter;

	public OsmNodeToFeaturePointConverter(FeaturePointCache featurePointCache) {
		this.featurePointCache = featurePointCache;
	}

	@Override
	public Feature<Point> convert(Node node) {
		Feature.FeatureBuilder<Point> featureBuilder = Feature.builder();
		Utils.setPropertiesForFeature(node, featureBuilder);
		featureBuilder.id(node.getId());
		featureBuilder.geometry(new Point(new Coordinate((float) node.getLongitude(), (float) node.getLatitude())));
		Feature<Point> feature = featureBuilder.build();
		featurePointCache.put(node.getId(), feature);
		if (feature.getProperties() != null) {
			return feature;
		}
		else {
			return null;
		}
	}

	@Override
	public void setNext(OsmToFeatureConverter<Node, Point> nextConverter) {
		this.nextConverter = nextConverter;
	}
}
