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

	private final FeatureLinestringCache featureLinestringCache;

	public OsmRelationToMultipolygonConverter(
			FeaturePolygonCache featurePolygonCache,
			FeatureLinestringCache featureLinestringCache) {
		this.featurePolygonCache = featurePolygonCache;
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
			List<Coordinate> ringGroup = new ArrayList<>();
			final Long ringGroupStartNodeId = startNodeIds.iterator().next();
			Long ringGroupEndNodeId = ringGroupStartNodeId;
			do {
				Feature<LineString> lineString  = lineStringUsageMap.remove(ringGroupEndNodeId);
				if (lineString == null) {
					throw new IllegalArgumentException("Polygon incomplete");
				}
				ringGroup.addAll(lineString.getGeometry().getCoordinates());
				ringGroupEndNodeId = Utils.getEndNode(lineString);
			} while (!ringGroupStartNodeId.equals(ringGroupEndNodeId));
			coordinates.add(Arrays.asList(ringGroup));
		}
	}

	private void handleRelationMember(RelationMember relationMember,
			Map<Long, Feature<LineString>> lineStringMap, List<List<List<Coordinate>>> coordinates) {
		if (relationMember.getMemberType() == EntityType.Way) {
			Feature<LineString> lineStringFeature = featureLinestringCache.getAndMarkDeleted(relationMember.getMemberId());
			if (lineStringFeature != null) {
				if (Utils.getEndNode(lineStringFeature).equals(Utils.getStartNode(lineStringFeature))) {
					coordinates.add(Arrays.asList(lineStringFeature.getGeometry().getCoordinates()));
					return;
				}
				lineStringMap.put(Utils.getStartNode(lineStringFeature),
						lineStringFeature);
				return;
			}
			Feature<Polygon> polygonFeature = featurePolygonCache.getAndMarkDeleted(relationMember.getMemberId());
			if (polygonFeature != null && !Utils.hasOnlyDefaultProperties(polygonFeature)) {
				coordinates.add(polygonFeature.getGeometry().getCoordinates());
			}
		}
	}
}
