package com.chandan.osmosis.plugin.geojson.converter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chandan.geojson.model.*;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;

public class OsmWayToFeatureLineStringConverter extends OsmToFeatureConverter<Way, LineString> {

	private final FeaturePointCache pointCache;

	private final FeatureLinestringCache lineStringCache;

	public OsmWayToFeatureLineStringConverter(FeaturePointCache pointCache, FeatureLinestringCache lineStringCache) {
		this.pointCache = pointCache;
		this.lineStringCache = lineStringCache;
	}

	@Override
	public boolean isValid(Feature<LineString> feature) {
		if (((LineStringProperties)feature.getProperties()).getHighway() != null) {
			return true;
		}
		return false;
	}

	@Override
	public Feature<LineString> getGeojsonModel(Way t) {
		if (t == null) {
			return null;
		}
		List<Coordinate> coordinates = new ArrayList<Coordinate>(t.getWayNodes().size());
		for (WayNode node : t.getWayNodes()) {
			Feature<Point> point = pointCache.get(node.getNodeId());
			if (point == null) {
				throw new IllegalStateException(MessageFormat.format(
						"Node id : {0} not present in cache, for way id : {1}",
						new Object[] { node.getNodeId(), t.getId() }));
			}
			coordinates.add(point.getGeometry().getCoordinates());
		}
		LineString lineString = new LineString();
		lineString.setCoordinates(coordinates);
		LineStringProperties wayProperties = getWayProperties(t);
		Feature<LineString> featureLineString = new Feature<LineString>(lineString, wayProperties);
		lineStringCache.put(t.getId(), featureLineString);
		return featureLineString;
	}

	private LineStringProperties getWayProperties(Way t) {
		LineStringProperties wayProperties = new LineStringProperties();
		Utils.populateCommonProperties(t, wayProperties);
		if ((t.getTags() != null && t.getTags().size() > 0)) {
			String highWay = ((TagCollection) t.getTags()).buildMap().get("highway");
			//String building = ((TagCollection) t.getTags()).buildMap().get("building");
			wayProperties.setHighway(highWay);
			wayProperties.setStartNodeId(t.getWayNodes().get(0).getNodeId());
			wayProperties.setEndNodeId(t.getWayNodes().get(t.getWayNodes().size() - 1).getNodeId());
			//wayProperties.setBuilding(building);
		}
		return wayProperties;
	}

}
