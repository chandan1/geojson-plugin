package com.chandan.osmosis.plugin.geojson;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

import java.util.HashMap;
import java.util.Map;

public class GeoJsonPluginLoader implements PluginLoader {

	@Override
	public Map<String, TaskManagerFactory> loadTaskFactories() {
		Map<String, TaskManagerFactory> map = new HashMap<String, TaskManagerFactory>();
		map.put("geojson-plugin", new GeoJsonTaskFactory());
		return map;
	}

}
