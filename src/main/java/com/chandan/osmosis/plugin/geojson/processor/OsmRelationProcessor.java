package com.chandan.osmosis.plugin.geojson.processor;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import com.chandan.geojson.model.MultiPolygon;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeatureMultiPolygonCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import com.chandan.osmosis.plugin.geojson.converter.OsmRelationToMultipolygonConverter;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;

import java.util.List;

/**
 * Created by chandan on 25/04/17.
 */
public class OsmRelationProcessor extends OsmEntityProcessor<Relation> {

	private final FeatureMultiPolygonCache featureMultiPolygonCache;

	private final OsmRelationToMultipolygonConverter osmRelationToMultipolygonConverter;

	public OsmRelationProcessor(
			FeaturePolygonCache featurePolygonCache,
			FeatureLinestringCache featureLinestringCache,
			FeatureMultiPolygonCache featureMultiPolygonCache) {
		this.featureMultiPolygonCache = featureMultiPolygonCache;
		this.osmRelationToMultipolygonConverter =  new OsmRelationToMultipolygonConverter(featurePolygonCache, featureLinestringCache);
	}

	@Override
	public void process(Relation relation) {
		Feature<MultiPolygon> polygon = osmRelationToMultipolygonConverter.convert(relation);
		if (polygon != null) {
			this.featureMultiPolygonCache.put(relation.getId(), polygon);
			return;
		}
	}
}
