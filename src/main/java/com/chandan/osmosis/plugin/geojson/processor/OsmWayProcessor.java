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

	private final OsmWayToFeatureLineStringConverter osmWayToFeatureLineStringConverter;

	private final OsmWayToFeaturePolygonConverter osmWayToFeaturePolygonConverter;

	private final FeatureLinestringCache featureLinestringCache;

	private final FeaturePolygonCache featurePolygonCache;

	public OsmWayProcessor(FeaturePointCache featurePointCache,
			FeatureLinestringCache featureLinestringCache,
			FeaturePolygonCache featurePolygonCache) {
		this.featureLinestringCache = featureLinestringCache;
		this.featurePolygonCache = featurePolygonCache;
		this.osmWayToFeaturePolygonConverter = new OsmWayToFeaturePolygonConverter(featurePointCache,
				featurePolygonCache);
		this.osmWayToFeatureLineStringConverter = new OsmWayToFeatureLineStringConverter(featurePointCache);
	}

	@Override
	public void process(Way way) {
		Feature<LineString> lineString = osmWayToFeatureLineStringConverter.convert(way);
		if (lineString != null) {
			this.featureLinestringCache.put(lineString.getId(), lineString);
			return;
		}
		Feature<Polygon> polygon = osmWayToFeaturePolygonConverter.convert(way);
		if (polygon != null) {
			this.featurePolygonCache.put(polygon.getId(), polygon);
		}
	}
}
