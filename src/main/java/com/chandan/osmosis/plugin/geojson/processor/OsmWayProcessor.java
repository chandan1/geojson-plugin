package com.chandan.osmosis.plugin.geojson.processor;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import com.chandan.geojson.model.LineString;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import com.chandan.osmosis.plugin.geojson.converter.OsmWayToFeatureLineStringConverter;
import com.chandan.osmosis.plugin.geojson.converter.OsmWayToFeaturePolygonConverter;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import java.util.List;

/**
 * Created by chandan on 5/5/16.
 */
public class OsmWayProcessor extends OsmEntityProcessor<Way> {


	private final FeatureWriter writer;

	private final OsmWayToFeatureLineStringConverter osmWayToFeatureLineStringConverter;

	private final OsmWayToFeaturePolygonConverter osmWayToFeaturePolygonConverter;

	public OsmWayProcessor(FeaturePointCache featurePointCache,
			FeatureLinestringCache featureLinestringCache,
			FeaturePolygonCache featurePolygonCache,
			FeatureWriter writer) {

		this.writer = writer;
		this.osmWayToFeaturePolygonConverter = new OsmWayToFeaturePolygonConverter(featurePointCache,
				featurePolygonCache);
		this.osmWayToFeatureLineStringConverter = new OsmWayToFeatureLineStringConverter(featurePointCache,
				featureLinestringCache);
	}

	@Override
	public void process(Way way) {
		List<Feature<LineString>> lineStrings = osmWayToFeatureLineStringConverter.convert(way);
		for (Feature<LineString> lineString : lineStrings) {
			writer.write(lineString);
		}
		List<Feature<Polygon>> polygons = osmWayToFeaturePolygonConverter.convert(way);
		for (Feature<Polygon> polygon : polygons) {
			writer.write(polygon);
		}
	}
}
