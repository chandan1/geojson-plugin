package com.chandan.osmosis.plugin.geojson.common;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Point;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openstreetmap.osmosis.core.domain.v0_6.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {

	public static String START_NODE_ID_TAG = "startNodeId";

	public static String END_NODE_ID_TAG = "endNodeId";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Tag areaTag = new Tag("area", "yes");

	private static final Tag naturalWater = new Tag("natural", "water");

	private static final Tag naturalWetLand = new Tag("natural", "wetland");

	public static String jsonEncode(Object o) throws JsonProcessingException {
		if (o == null)
			return "";
		return objectMapper.writeValueAsString(o);
	}

	@SuppressWarnings("unchecked")
	public static <T> T jsonDecode(byte[] data, TypeReference<T> t) throws Exception {

		if (data == null) {
			return null;
		}
		return (T) objectMapper.readValue(data, t);
	}

	public static void setPropertiesForFeature(Entity entity, Feature.FeatureBuilder featureBuilder) {
		Objects.requireNonNull(entity, "entity cannot be null");
		Objects.requireNonNull(featureBuilder, "featureBuilder cannot be null");
		if (entity.getTags() != null && entity.getTags().size() > 0) {
			Map<String, String> tagsMap = ((TagCollection) entity.getTags()).buildMap();
			ImmutableMap.Builder mapBuilder = ImmutableMap.<String, Object>builder();
			for (Map.Entry<String, String> entry : tagsMap.entrySet()) {
				if (entry.getKey() == "created_by") {
					continue;
				}
				mapBuilder.put(entry.getKey(), entry.getValue());
			}
			if (entity.getType() == EntityType.Way) {
				mapBuilder.put(START_NODE_ID_TAG, ((Way) entity).getWayNodes().get(0).getNodeId());
				mapBuilder.put(END_NODE_ID_TAG, (((Way) entity).getWayNodes().get(((Way) entity).getWayNodes().size() - 1).getNodeId()));
			}
			Map<String, Object> properties = mapBuilder.build();
			featureBuilder.properties(properties.size() > 0 ? properties : null);
		}
	}

	public static boolean isPolygon(Way way) {
		Objects.requireNonNull(way, "way cannot be null");
		Objects.requireNonNull(way.getWayNodes(), "wayNodes cannot be null");
		if (way.getWayNodes().size() > 0 ) {
			if (way.getWayNodes().get(0).getNodeId() == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()) {
				if (way.getTags().contains(areaTag)) {
					return true;
				}
				if (way.getTags().contains(naturalWater)) {
					return true;
				}
				if (way.getTags().contains(naturalWetLand)) {
					return true;
				}
			}
		}
		return false;
	}
}
