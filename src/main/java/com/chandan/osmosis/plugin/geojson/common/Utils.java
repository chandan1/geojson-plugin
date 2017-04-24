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

	private static ObjectMapper objectMapper = new ObjectMapper();

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
				mapBuilder.put(entry.getKey(), entry.getValue());
			}
			featureBuilder.properties(mapBuilder.build());
		}
	}

	public static boolean isPolygon(Way way) {
		Objects.requireNonNull(way, "way cannot be null");
		Objects.requireNonNull(way.getWayNodes(), "wayNodes cannot be null");
		if (way.getWayNodes().size() > 0 ) {
			if (way.getWayNodes().get(0).getNodeId() == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()
					&& way.getTags().contains(new Tag("area", "yes"))) {
				return true;
			}
		}
		return false;
	}
}
