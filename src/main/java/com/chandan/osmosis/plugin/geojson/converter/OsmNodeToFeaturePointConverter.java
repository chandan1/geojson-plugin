package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Coordinate;
import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.chandan.geojson.model.PointProperties;
import lombok.NoArgsConstructor;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;

import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import org.openstreetmap.osmosis.core.store.ObjectDataInputIterator;

import java.util.HashMap;
import java.util.Map;


public class OsmNodeToFeaturePointConverter implements OsmToFeatureConverter<Node, Point> {

	private final FeaturePointCache featurePointCache;
	private OsmToFeatureConverter<Node, Point> nextConverter;
	private final FeaturePropertyBuilder<Node, Point> featurePropertyBuilder;

	public OsmNodeToFeaturePointConverter(FeaturePointCache featurePointCache) {
		this.featurePointCache = featurePointCache;
		this.featurePropertyBuilder = FeaturePropertyBuilderRegistry.instance().getPropertyBuilder(Node.class, Point.class);
	}

	@Override
	public Feature<Point> convert(Node node) {
		Feature.FeatureBuilder<Point> featureBuilder = Feature.builder();
		setProperties(node, featureBuilder);
		featureBuilder.id(String.valueOf(node.getId()));
		featureBuilder.geometry(new Point(new Coordinate((float) node.getLongitude(), (float) node.getLatitude())));
		Feature<Point> feature = featureBuilder.build();
		featurePointCache.put(node.getId(), feature);
		if (feature.getProperties() != null) {
			return feature;
		} else {
			return null;
		}
	}

	@Override
	public void setNext(OsmToFeatureConverter<Node, Point> nextConverter) {
		this.nextConverter = nextConverter;
	}

	@Override
	public void setProperties(Node t, Feature.FeatureBuilder<Point> featureBuilder) {
		if (featurePropertyBuilder != null) {
			featurePropertyBuilder.getProperties(t, featureBuilder);
			return;
		}
		if ((t.getTags() != null && t.getTags().size() > 0)) {
			Map<String, String> tagsMap = ((TagCollection) t.getTags()).buildMap();
			Map<String, Object> properties = new HashMap<>();
			for (Map.Entry<String, String> entry : tagsMap.entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
			}
			featureBuilder.properties(properties);
		}
	}
}
