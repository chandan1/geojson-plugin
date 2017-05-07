package com.chandan.osmosis.plugin.geojson.processor;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import com.chandan.geojson.model.MultiPolygon;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
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

	private final FeatureWriter featureWriter;

	private final FeaturePolygonCache featurePolygonCache;

	private final FeatureLinestringCache featureLinestringCache;

	private final FeaturePointCache featurePointCache;

	private final OsmRelationToMultipolygonConverter osmRelationToMultipolygonConverter;

	public OsmRelationProcessor(FeatureWriter featureWriter,
			FeaturePolygonCache featurePolygonCache,
			FeatureLinestringCache featureLinestringCache,
			FeaturePointCache featurePointCache,
			OsmRelationToMultipolygonConverter osmRelationToMultipolygonConverter) {
		this.featureWriter = featureWriter;
		this.featurePolygonCache = featurePolygonCache;
		this.featureLinestringCache = featureLinestringCache;
		this.featurePointCache = featurePointCache;
		this.osmRelationToMultipolygonConverter = osmRelationToMultipolygonConverter;
	}

	@Override
	public void process(Relation relation) {
		List<Feature<Polygon>> polygons = osmRelationToMultipolygonConverter.convert(relation);
		for (Feature<Polygon> polygon : polygons) {
			featureWriter.write(polygon);
		}
	}
}
