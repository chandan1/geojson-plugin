package com.chandan.osmosis.plugin.geojson.writer;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;

import java.util.List;
import java.util.Map;

/**
 * Created by chandan on 12/06/16.
 */
public abstract class FeatureWriter {

	public abstract void write(Feature<? extends Geometry> feature);

	public abstract void complete();

	public abstract String getName();

	public void register() {
		FeatureWriterRegistry.instance().registerWriter(getName(), this);
	}
}
