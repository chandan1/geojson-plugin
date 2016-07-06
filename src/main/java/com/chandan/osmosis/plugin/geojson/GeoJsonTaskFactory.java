package com.chandan.osmosis.plugin.geojson;

import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriterRegistry;
import com.chandan.osmosis.plugin.geojson.writer.FileFeatureWriter;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonTaskFactory extends TaskManagerFactory {

	private static final String GEO_JSON_FILE_ARG = "geojsonFile";

	private static final String DIRECTORY_FOR_CACHE = "directoryForCache";

	private static final String GEO_JSON_WRITER = "geojson-writer";

	@Override
	protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
		new FileFeatureWriter().register();

		String geoJsonWriter = getStringArgument(taskConfig, GEO_JSON_WRITER);
		FeatureWriter featureWriter = FeatureWriterRegistry.instance().getFileWriter(geoJsonWriter);

		Map<String, String> paramValues = new HashMap<>();
		List<String> params = featureWriter.getArguments();
		for (String param : params) {
			paramValues.put(param, getStringArgument(taskConfig, param));
		}
		featureWriter.init(paramValues);
		String directoryForCache = getStringArgument(taskConfig, DIRECTORY_FOR_CACHE);
		GeoJsonSink geoJsonSink = new GeoJsonSink(featureWriter, directoryForCache);
		return new SinkManager(taskConfig.getId(), geoJsonSink, taskConfig.getPipeArgs());
	}
}
