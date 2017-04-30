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

}
