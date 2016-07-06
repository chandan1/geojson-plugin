package com.chandan.osmosis.plugin.geojson.processor;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.LineString;
import com.chandan.geojson.model.LineStringProperties;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.chandan.osmosis.plugin.geojson.converter.FeaturePropertyBuilder;
import com.chandan.osmosis.plugin.geojson.converter.OsmWayToFeatureLineStringConverter;
import com.chandan.osmosis.plugin.geojson.converter.OsmWayToFeaturePolygonConverter;
import com.chandan.osmosis.plugin.geojson.processor.OsmEntityProcessor;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import java.io.OutputStreamWriter;
import java.text.MessageFormat;

/**
 * Created by chandan on 5/5/16.
 */
public class OsmWayProcessor extends OsmEntityProcessor<Way> {

    private final FeaturePointCache featurePointCache;

    private final FeatureLinestringCache featureLinestringCache;

    private final FeatureWriter writer;

    private final OsmWayToFeatureLineStringConverter osmWayToFeatureLineStringConverter;

    private final OsmWayToFeaturePolygonConverter osmWayToFeaturePolygonConverter;

    public OsmWayProcessor(FeaturePointCache featurePointCache,
                           FeatureLinestringCache featureLinestringCache,
                           FeatureWriter writer
                           ) {
        this.featurePointCache = featurePointCache;
        this.featureLinestringCache = featureLinestringCache;
        this.writer = writer;
        this.osmWayToFeatureLineStringConverter = new OsmWayToFeatureLineStringConverter(featurePointCache, featureLinestringCache);
        this.osmWayToFeaturePolygonConverter = new OsmWayToFeaturePolygonConverter(featurePointCache, featureLinestringCache);
    }

    @Override
    public void process(Way way) {
        Feature<LineString> lineStringFeature = osmWayToFeatureLineStringConverter.convert(way);
        if (lineStringFeature != null) {
            writer.write(lineStringFeature);
        } else {

        }
    }
}
