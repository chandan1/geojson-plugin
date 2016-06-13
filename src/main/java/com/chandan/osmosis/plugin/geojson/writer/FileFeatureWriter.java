package com.chandan.osmosis.plugin.geojson.writer;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Geometry;
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
    private final List<String> arguments = ImmutableList.of("geojson-file");
    private OutputStreamWriter writer;
    private Map<String, String> params;

    @Override
    public void write(Feature<? extends Geometry> feature) {

    }

    @Override
    public void init(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public void open() {

        try {
            String geoJsonFile = params.get("geojson-file");
            writer = new OutputStreamWriter(new FileOutputStream(new File(geoJsonFile)));
        } catch (Exception e) {
            throw new OsmosisRuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new OsmosisRuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }
}
