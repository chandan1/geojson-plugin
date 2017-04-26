package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.*;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;

import java.util.ArrayList;
import java.util.List;

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
		for (RelationMember relationMember : relation.getMembers()) {
			if (relationMember.getMemberRole() == ""
					|| relationMember.getMemberRole() == "outer") {
				Feature<Polygon> polygonFeature = featurePolygonCache.get(relationMember.getMemberId());
				Feature<LineString> lineStringFeature = featureLinestringCache.get(relationMember.getMemberId());

				if (relationMember.getMemberType() == EntityType.Way && polygonFeature != null) {

				}
			}
		}
		return null;
	}


}
