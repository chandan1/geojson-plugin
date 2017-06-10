package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.*;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.google.common.collect.ImmutableMap;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;

import javax.sound.sampled.Line;
import java.util.*;

/**
 * Created by chandan on 25/04/17.
 */
public class OsmRelationToMultipolygonConverter implements OsmToFeatureConverter<Relation, MultiPolygon> {

	private final FeaturePolygonCache featurePolygonCache;

	private final FeaturePointCache featurePointCache;

	private final FeatureLinestringCache featureLinestringCache;

	public OsmRelationToMultipolygonConverter(
			FeaturePolygonCache featurePolygonCache,
			FeaturePointCache featurePointCache,
			FeatureLinestringCache featureLinestringCache) {
		this.featurePolygonCache = featurePolygonCache;
		this.featurePointCache = featurePointCache;
		this.featureLinestringCache = featureLinestringCache;
	}

	@Override
	public Feature<MultiPolygon> convert(Relation relation) {
		List<List<List<Coordinate>>> coordinates = new ArrayList<>();
		Map<Long, Feature<LineString>> outerStartNodeIdLineStringMap = new HashMap<>();
		Map<Long, Feature<LineString>> innerStartNodeIdLineStringMap = new HashMap<>();
		Iterator<RelationMember> relationMembersIt = relation.getMembers().iterator();

		while (relationMembersIt.hasNext()){
			RelationMember relationMember = relationMembersIt.next();
			if ("".equals(relationMember.getMemberRole())
					|| "outer".equals(relationMember.getMemberRole())) {
				handleRelationMember(relationMember, outerStartNodeIdLineStringMap, coordinates);
			}
			if ("inner".equals(relationMember.getMemberRole())) {
				handleRelationMember(relationMember, innerStartNodeIdLineStringMap, coordinates);
			}
		}
		addPolygonsFromLineString(outerStartNodeIdLineStringMap, coordinates);
		addPolygonsFromLineString(innerStartNodeIdLineStringMap, coordinates);
		Feature.FeatureBuilder<MultiPolygon> featureBuilder = Feature.builder();
		featureBuilder.geometry(new MultiPolygon(coordinates));
		Utils.setPropertiesForFeature(relation, featureBuilder);
		featureBuilder.id(relation.getId());
		return featureBuilder.build();
	}

	private void addPolygonsFromLineString(Map<Long, Feature<LineString>> lineStringUsageMap, List<List<List<Coordinate>>> coordinates) {
		Set<Long> startNodeIds = lineStringUsageMap.keySet();
		while (!startNodeIds.isEmpty()) {
			List<Coordinate> ring = new ArrayList<>();
			final Long ringGroupStartNodeId = startNodeIds.iterator().next();
			Feature<LineString> lineString = lineStringUsageMap.get(ringGroupStartNodeId);
			if (lineString == null) {
				throw new IllegalArgumentException("Polygon incomplete");
			}
			Long currentEndNodeId = Utils.getEndNode(lineString);
			while (!currentEndNodeId.equals(ringGroupStartNodeId)) {
				ring.addAll(lineString.getGeometry().getCoordinates());
				lineString = lineStringUsageMap.get(currentEndNodeId);
				if (lineString == null) {
					throw new IllegalArgumentException("Polygon incomplete");
				}
				currentEndNodeId = Utils.getEndNode(lineString);
			}
			if (!currentEndNodeId.equals(ringGroupStartNodeId)) {
				throw new IllegalArgumentException("Polygon incomplete");
			}
			coordinates.add(Arrays.asList(ring));
			startNodeIds.remove(ringGroupStartNodeId);
		}
		if (!startNodeIds.isEmpty()) {
			throw new IllegalArgumentException("Polygon incomplete");
		}
	}

	private void handleRelationMember(RelationMember relationMember,
			Map<Long, Feature<LineString>> lineStringMap, List<List<List<Coordinate>>> coordinates) {
		if (relationMember.getMemberType() == EntityType.Way) {
			Feature<LineString> lineStringFeature = featureLinestringCache.get(relationMember.getMemberId());
			if (lineStringFeature != null) {
				if (Utils.getEndNode(lineStringFeature).equals(Utils.getStartNode(lineStringFeature))) {
					coordinates.add(Arrays.asList(lineStringFeature.getGeometry().getCoordinates()));
					return;
				}
				lineStringMap.put(Utils.getStartNode(lineStringFeature),
						lineStringFeature);
				return;
			}
			Feature<Polygon> polygonFeature = featurePolygonCache.get(relationMember.getMemberId());
			if (polygonFeature != null && !Utils.hasOnlyDefaultProperties(polygonFeature)) {
				coordinates.add(polygonFeature.getGeometry().getCoordinates());
			}
		}
	}
}
