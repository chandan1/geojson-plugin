package com.chandan.osmosis.plugin.geojson;

import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriterRegistry;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

public class GeoJsonTaskFactory extends TaskManagerFactory {

	private static final String DIRECTORY_FOR_CACHE = "directoryForCache";

	private static final String GEO_JSON_WRITER = "geojson-writer";

	@Override
	protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
		String geoJsonWriter = getStringArgument(taskConfig, GEO_JSON_WRITER);
		FeatureWriter featureWriter = FeatureWriterRegistry.instance().getFileWriter(geoJsonWriter);

		String directoryForCache = getStringArgument(taskConfig, DIRECTORY_FOR_CACHE);
		GeoJsonSink geoJsonSink = new GeoJsonSink(featureWriter, directoryForCache);
		return new SinkManager(taskConfig.getId(), geoJsonSink, taskConfig.getPipeArgs());
	}
}
