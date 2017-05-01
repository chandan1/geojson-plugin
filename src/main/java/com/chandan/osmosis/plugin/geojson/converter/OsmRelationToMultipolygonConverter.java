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

import java.util.*;

/**
 * Created by chandan on 25/04/17.
 */
public class OsmRelationToMultipolygonConverter implements OsmToFeatureConverter<Relation, Polygon> {

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
	public List<Feature<Polygon>> convert(Relation relation) {
		List<Feature<Polygon>> polygons = new ArrayList<>();
		Map<Long, LineStringUsage> outerStartNodeIdLineStringMap = new HashMap<>();
		Map<Long, LineStringUsage> innerStartNodeIdLineStringMap = new HashMap<>();
		Iterator<RelationMember> relationMembersIt = relation.getMembers().iterator();

		while (relationMembersIt.hasNext()){
			RelationMember relationMember = relationMembersIt.next();
			if (relationMember.getMemberRole() == ""
					|| relationMember.getMemberRole() == "outer") {
				handleRelationMember(relationMember, outerStartNodeIdLineStringMap, polygons);
			}
			if (relationMember.getMemberRole() == "inner") {
				handleRelationMember(relationMember, innerStartNodeIdLineStringMap, polygons);
			}
		}
		return polygons;
	}

	private void addRingGroups(Map<Long, LineStringUsage> lineStringUsageMap, List<Feature<Polygon>> polygons) {
		Set<Long> startNodeIds = lineStringUsageMap.keySet();
		while (!startNodeIds.isEmpty()) {
			List<Coordinate> ring = new ArrayList<>();
			final Long ringGroupStartNodeId = startNodeIds.iterator().next();
			LineStringUsage lineStringUsage = lineStringUsageMap.get(ringGroupStartNodeId);
			if (lineStringUsage == null) {
				throw new IllegalArgumentException("Polygon incomplete");
			}
			Long currentEndNodeId = Utils.getEndNode(lineStringUsage.lineStringFeature);
			while (!currentEndNodeId.equals(ringGroupStartNodeId)) {
				lineStringUsage.usageCount++;
				ring.addAll(lineStringUsage.lineStringFeature.getGeometry().getCoordinates());
				lineStringUsage = lineStringUsageMap.get(currentEndNodeId);
				if (lineStringUsage == null) {
					throw new IllegalArgumentException("Polygon incomplete");
				}
				currentEndNodeId = Utils.getEndNode(lineStringUsage.lineStringFeature);
			}
			if (!currentEndNodeId.equals(ringGroupStartNodeId)) {
				throw new IllegalArgumentException("Polygon incomplete");
			}
			polygons.add(
					Feature.<Polygon>builder()
							.geometry(new Polygon(Arrays.asList(ring)))
							.properties(ImmutableMap.<String, Object>of(Utils.START_NODE_ID_TAG, ringGroupStartNodeId,
									Utils.END_NODE_ID_TAG, currentEndNodeId))
							.build());
			startNodeIds.remove(ringGroupStartNodeId);
		}
	}

	private void handleRelationMember(RelationMember relationMember,
			Map<Long, LineStringUsage> lineStringUsageMap, List<Feature<Polygon>> preexistingPolygons) {
		if (relationMember.getMemberType() == EntityType.Way) {
			Feature<LineString> lineStringFeature = featureLinestringCache.get(relationMember.getMemberId());
			if (lineStringFeature != null) {
				lineStringUsageMap.put(Utils.getStartNode(lineStringFeature),
						new LineStringUsage(lineStringFeature));
				return;
			}
			Feature<Polygon> polygonFeature = featurePolygonCache.get(relationMember.getMemberId());
			if (polygonFeature != null && !Utils.hasOnlyDefaultProperties(polygonFeature)) {
				preexistingPolygons.add(polygonFeature);
			}
		}
	}

	private static class LineStringUsage {
		private Feature<LineString> lineStringFeature;
		private int usageCount = 0;

		public LineStringUsage(Feature<LineString> lineStringFeature) {
			this.lineStringFeature = lineStringFeature;
		}
	}

}
