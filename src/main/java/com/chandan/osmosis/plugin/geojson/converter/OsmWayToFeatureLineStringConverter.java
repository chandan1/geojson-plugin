package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Coordinate;
import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.chandan.geojson.model.Point;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsmWayToFeatureLineStringConverter implements OsmToFeatureConverter<Way, LineString> {

	private final FeaturePointCache pointCache;

	private final FeatureLinestringCache lineStringCache;

	private final FeaturePropertyBuilder<Way, LineString> featurePropertyBuilder;

	private OsmToFeatureConverter<Way, LineString> nextConverter;

	public OsmWayToFeatureLineStringConverter(FeaturePointCache pointCache,
			FeatureLinestringCache lineStringCache) {
		this.pointCache = pointCache;
		this.lineStringCache = lineStringCache;
		this.featurePropertyBuilder = FeaturePropertyBuilderRegistry.instance()
				.getPropertyBuilder(Way.class, LineString.class);
	}

	@Override
	public Feature<LineString> convert(Way t) {
		if (t == null && (t.getWayNodes() == null || t.getWayNodes().size() <= 1)) {
			return null;
		}
		if (t.getWayNodes().get(0).getNodeId() == t.getWayNodes().get(t.getWayNodes().size() - 1).getNodeId()
				&& t.getTags().contains(new Tag("area", "yes"))) {
			if (this.nextConverter != null) {
				return this.nextConverter.convert(t);
			}
			else {
				return null;
			}
		}

		Feature.FeatureBuilder<LineString> featureBuilder = Feature.builder();

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
		featureBuilder.geometry(new LineString(coordinates));
		featureBuilder.id(String.valueOf(t.getId()));
		setProperties(t, featureBuilder);
		Feature<LineString> feature = featureBuilder.build();
		lineStringCache.put(t.getId(), feature);
		if (feature.getProperties() != null) {
			return feature;
		}
		else {
			return null;
		}
	}

	@Override
	public void setNext(OsmToFeatureConverter<Way, LineString> nextConverter) {
		this.nextConverter = nextConverter;
	}

	@Override
	public void setProperties(Way t, Feature.FeatureBuilder<LineString> featureBuilder) {
		if (featurePropertyBuilder != null) {
			featurePropertyBuilder.getProperties(t, featureBuilder);
			return;
		}

		if ((t.getTags() != null && t.getTags().size() > 0)) {
			Map<String, Object> properties = new HashMap<>();
			Map<String, String> tagsMap = ((TagCollection) t.getTags()).buildMap();
			for (Map.Entry<String, String> entry : tagsMap.entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
			}
			featureBuilder.properties(properties);
		}
	}
}
