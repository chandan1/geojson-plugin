package com.chandan.osmosis.plugin.geojson;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import com.google.common.collect.ImmutableList;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by chandan on 12/06/16.
 */
public class FileFeatureWriter extends FeatureWriter {

	private final String name = "file-writer";

	private final OutputStreamWriter writer;

	public FileFeatureWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

	@Override
	public void write(Feature<? extends Geometry> feature) {
		try {
			writer.write(Utils.jsonEncode(feature) + "\n");
		}
		catch (Exception e) {
			throw new OsmosisRuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void complete() {
		System.out.println("FileFeatureWriter completed");
	}
}
