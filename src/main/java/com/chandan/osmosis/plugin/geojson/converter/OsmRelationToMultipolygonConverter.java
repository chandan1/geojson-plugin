package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.*;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.store.PeekableIterator;

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
		List<List<List<Coordinate>>> ringGroup = new ArrayList<>();
		Set<Long> usedRelationMembers = new HashSet<>();
		PeekingIterator<RelationMember> relationMembersIt = Iterators.peekingIterator(relation.getMembers().iterator());

		while (relationMembersIt.hasNext()){
			List<RelationMember> outerRelationMembers = new ArrayList<>();
			List<RelationMember> innerRelationMembers = new ArrayList<>();

			while (relationMembersIt.peek().getMemberRole() == ""
					|| relationMembersIt.peek().getMemberRole() == "outer") {
				outerRelationMembers.add(relationMembersIt.next());
			}
			while (relationMembersIt.peek().getMemberRole() == "inner") {
				innerRelationMembers.add(relationMembersIt.next());
			}
		}
		return null;
	}


	private List<Feature<Polygon>> getOuterPolygons(List<RelationMember> outerRelationMembers, Set<Long> usedRelationMembers) {
		Map<Long, LineStringUsage> startNodeMap = new HashMap<>();

		List<Feature<Polygon>> polygons = new ArrayList<>();
		for (RelationMember relationMember : outerRelationMembers) {
			if (relationMember.getMemberType() == EntityType.Way) {
				Feature<Polygon> polygonFeature = featurePolygonCache.get(relationMember.getMemberId());
				if (polygonFeature != null) {
					usedRelationMembers.add(relationMember.getMemberId());
					if (Utils.hasOnlyDefaultProperties(polygonFeature)) {
						continue;
					}
					polygons.add(polygonFeature);
					continue;
				}
				Feature<LineString> lineStringFeature = featureLinestringCache.get(relationMember.getMemberId());
				startNodeMap.put(Utils.getStartNode(lineStringFeature), new LineStringUsage(lineStringFeature, 0));
			}
		}
		while (!startNodeMap.isEmpty()) {
			Long originalStartNodeId = startNodeMap.keySet().iterator().next();
			Long nextStartNodeId = originalStartNodeId;
			LineStringUsage lineStringUsage = startNodeMap.get(nextStartNodeId);
			List<Coordinate> coordinates = new ArrayList<>();
			while (lineStringUsage != null && lineStringUsage.usageCount == 0) {
				coordinates.addAll(lineStringUsage.lineStringFeature.getGeometry().getCoordinates());
				lineStringUsage.usageCount++;
				usedRelationMembers.add(Utils.getOsmId(lineStringUsage.lineStringFeature));
				nextStartNodeId = Utils.getEndNode(lineStringUsage.lineStringFeature);
				lineStringUsage = startNodeMap.get(nextStartNodeId);
			}
			if (!originalStartNodeId.equals(nextStartNodeId)) {
				return null;
			}
		}
		return polygons;
	}

	private static class LineStringUsage {

		Feature<LineString> lineStringFeature;
		int usageCount;

		public LineStringUsage(Feature<LineString> lineStringFeature, int usageCount) {
			this.lineStringFeature = lineStringFeature;
			this.usageCount = usageCount;
		}
	}
}
