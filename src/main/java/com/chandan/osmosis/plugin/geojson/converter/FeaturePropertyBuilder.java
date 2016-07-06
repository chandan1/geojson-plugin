package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

/**
 * Created by chandan on 03/07/16.
 */
public interface FeaturePropertyBuilder<T extends Entity, U extends Geometry> {
	void getProperties(T t, Feature.FeatureBuilder<U> featureBuilder);
}
