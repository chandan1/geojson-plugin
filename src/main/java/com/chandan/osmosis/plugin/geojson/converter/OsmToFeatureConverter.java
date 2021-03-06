package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

import java.util.List;

public interface OsmToFeatureConverter<T extends Entity, U extends Geometry> {

	Feature<U> convert(T t);
}
