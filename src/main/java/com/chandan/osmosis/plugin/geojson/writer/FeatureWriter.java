package com.chandan.osmosis.plugin.geojson.writer;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;

/**
 * Created by chandan on 12/06/16.
 */
public interface FeatureWriter {
    void write(Feature<? extends Geometry> feature);
}
