package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Coordinate;
import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.chandan.geojson.model.PointProperties;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;

import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;

public class OsmNodeToFeaturePointConverter extends OsmToFeatureConverter<Node, Point> {

	private final FeaturePointCache featurePointCache;

	public OsmNodeToFeaturePointConverter(FeaturePointCache featurePointCache) {
		this.featurePointCache = featurePointCache;
	}

	@Override
	public Feature<Point> getGeojsonModel(Node node) {
		PointProperties properties = getNodeProperties(node);
		Feature<Point> pointFeature =  new Feature<Point>(new Point(
					new Coordinate((float) node.getLongitude(), (float) node.getLatitude())), properties);
		featurePointCache.put(node.getId(), pointFeature);
		return pointFeature;
	}

	@Override
	public boolean isValid(Feature<Point> feature) {
		if (feature.getProperties() != null) {
			return true;
		}
		return false;
	}

	private PointProperties getNodeProperties(Node t) {
		if ((t.getTags() != null && t.getTags().size() > 0)) {
			PointProperties nodeProperties = new PointProperties();
			Utils.populateCommonProperties(t, nodeProperties);
			if (!Utils.hasCommonProperty(nodeProperties)) {
				return null;
			}
			String amenity = ((TagCollection) t.getTags()).buildMap().get("amenity");
			String highWay = ((TagCollection) t.getTags()).buildMap().get("highway");
			nodeProperties.setAmenity(amenity);
			nodeProperties.setHighway(highWay);
			return nodeProperties;
		}
		return null;
	}
}
