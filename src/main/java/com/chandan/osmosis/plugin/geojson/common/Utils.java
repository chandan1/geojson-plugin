package com.chandan.osmosis.plugin.geojson.common;

import com.chandan.geojson.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openstreetmap.osmosis.core.domain.v0_6.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Utils {

	public static String START_NODE_ID_TAG = "startNodeId";

	public static String END_NODE_ID_TAG = "endNodeId";

	public static String DELETED = "deleted";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<List<Tag>> areaTagsList = ImmutableList.<List<Tag>>of(
			ImmutableList.of(new Tag("area", "yes")),
			ImmutableList.of(new Tag("natural", "water")),
			ImmutableList.of(new Tag("natural", "wetland")),
			ImmutableList.of(new Tag("natural", "reservoir")),
			ImmutableList.of(new Tag("waterway", "riverbank"))
	);

	public static void markDeleted(Feature<? extends Geometry> feature) {
		Objects.requireNonNull(feature, "feature cannot be null");
		feature.getProperties().put(DELETED, Boolean.TRUE);
	}

	public static boolean isMarkedDeleted(Feature<? extends Geometry> feature) {
		Objects.requireNonNull(feature, "feature cannot be null");
		Map<String, Object> properties = feature.getProperties();
		if (properties == null) {
			return false;
		}
		return Boolean.TRUE.equals(properties.get(DELETED));
	}

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
		ImmutableMap.Builder mapBuilder = ImmutableMap.<String, Object>builder();
		if (entity.getType() == EntityType.Way) {
			mapBuilder.put(START_NODE_ID_TAG, (((Way) entity).getWayNodes().get(0).getNodeId()));
			mapBuilder.put(END_NODE_ID_TAG, (((Way) entity).getWayNodes().get(((Way) entity).getWayNodes().size() - 1).getNodeId()));
		}

		if (entity.getTags() != null && entity.getTags().size() > 0) {
			Map<String, String> tagsMap = ((TagCollection) entity.getTags()).buildMap();

			for (Map.Entry<String, String> entry : tagsMap.entrySet()) {
				if (entry.getKey() == "created_by") {
					continue;
				}
				mapBuilder.put(entry.getKey(), entry.getValue());
			}
		}
		Map<String, Object> properties = mapBuilder.build();
		featureBuilder.properties(properties);
	}

	public static boolean hasOnlyDefaultProperties(Feature<? extends GeoJson> feature) {
		Objects.requireNonNull(feature, "feature cannot be null");
		return feature.getProperties() != null
				&& feature.getProperties().size() == 2
				&& feature.getProperties().containsKey(START_NODE_ID_TAG)
				&& feature.getProperties().containsKey(END_NODE_ID_TAG);
	}

	public static Long getEndNode(Feature<LineString> lineStringFeature) {
		Objects.requireNonNull(lineStringFeature, "lineStringFeature cannot be null");
		if (lineStringFeature.getProperties() != null) {
			return ((Number)lineStringFeature.getProperties().get(END_NODE_ID_TAG)).longValue();
		}
		return null;
	}

	public static Long getStartNode(Feature<LineString> lineStringFeature) {
		Objects.requireNonNull(lineStringFeature, "lineStringFeature cannot be null");
		if (lineStringFeature.getProperties() != null) {
			return ((Number)lineStringFeature.getProperties().get(START_NODE_ID_TAG)).longValue();
		}
		return null;
	}

	public static boolean isPolygon(Way way) {
		Objects.requireNonNull(way, "way cannot be null");
		Objects.requireNonNull(way.getWayNodes(), "wayNodes cannot be null");
		if (way.getWayNodes().size() > 0 ) {
			if (way.getWayNodes().get(0).getNodeId() == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()) {
				if (way.getTags() != null) {
					Map<String, String> wayTagMap = new HashMap<>(way.getWayNodes().size());
					for (Tag tag : way.getTags()) {
						wayTagMap.put(tag.getKey(), tag.getValue());
					}
					for (List<Tag> areaTags : areaTagsList) {
						boolean isPolygon = true;
						for (Tag tag : areaTags) {
							String value = wayTagMap.get(tag.getKey());
							if (value == null || !value.equals(tag.getValue())) {
								isPolygon = false;
								break;
							}
						}
						if (isPolygon) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
