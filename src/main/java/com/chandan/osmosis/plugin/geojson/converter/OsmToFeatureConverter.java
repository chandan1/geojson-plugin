package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;


public abstract class OsmToFeatureConverter<T extends Entity, U extends Geometry> {

	public abstract Feature<U> getGeojsonModel(T t);

	public abstract boolean isValid(Feature<U> feature);
}
