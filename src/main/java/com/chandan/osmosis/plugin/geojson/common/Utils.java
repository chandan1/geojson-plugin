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

	private static String OSM_ID = "id";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<Tag> areaTags = ImmutableList.of(new Tag("area", "yes"),
			new Tag("natural", "water"), new Tag("natural", "wetland"));

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
			mapBuilder.put(OSM_ID, entity.getId());
			if (entity.getType() == EntityType.Way) {
				mapBuilder.put(START_NODE_ID_TAG, ((Way) entity).getWayNodes().get(0).getNodeId());
				mapBuilder.put(END_NODE_ID_TAG, (((Way) entity).getWayNodes().get(((Way) entity).getWayNodes().size() - 1).getNodeId()));
			}

			Map<String, Object> properties = mapBuilder.build();
			featureBuilder.properties(properties.size() > 0 ? properties : null);
		}
	}

	public static boolean hasOnlyDefaultProperties(Feature<Polygon> polygonFeature) {
		Objects.requireNonNull(polygonFeature, "polygonFeature cannot be null");
		return polygonFeature.getProperties() != null
				&& polygonFeature.getProperties().size() == 2
				&& polygonFeature.getProperties().containsKey(START_NODE_ID_TAG)
				&& polygonFeature.getProperties().containsKey(END_NODE_ID_TAG);
	}

	public static Long getEndNode(Feature<LineString> lineStringFeature) {
		Objects.requireNonNull(lineStringFeature, "lineStringFeature cannot be null");
		if (lineStringFeature.getProperties() != null) {
			return (Long)lineStringFeature.getProperties().get(END_NODE_ID_TAG);
		}
		return null;
	}

	public static Long getStartNode(Feature<LineString> lineStringFeature) {
		Objects.requireNonNull(lineStringFeature, "lineStringFeature cannot be null");
		if (lineStringFeature.getProperties() != null) {
			return (Long)lineStringFeature.getProperties().get(START_NODE_ID_TAG);
		}
		return null;
	}

	public static boolean isPolygon(Way way) {
		Objects.requireNonNull(way, "way cannot be null");
		Objects.requireNonNull(way.getWayNodes(), "wayNodes cannot be null");
		if (way.getWayNodes().size() > 0 ) {
			if (way.getWayNodes().get(0).getNodeId() == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()) {
				if (way.getTags() != null) {
					for (Tag tag : way.getTags()) {
						for (Tag areaTag : areaTags) {
							if (tag.compareTo(areaTag) == 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static Long getOsmId(Feature<? extends Geometry> feature) {
		Objects.requireNonNull(feature, "feature cannot be null");
		if (feature.getProperties() != null) {
			feature.getProperties().get(OSM_ID);
		}
		return null;
	}

}
