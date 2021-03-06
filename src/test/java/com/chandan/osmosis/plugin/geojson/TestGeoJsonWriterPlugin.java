package com.chandan.osmosis.plugin.geojson;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.osmosis.core.Osmosis;

import java.io.*;

/**
 * Created by chandan on 5/4/16.
 */
public class TestGeoJsonWriterPlugin {

	@Test
	public void testGeoJsonWriter() throws Exception {
		String osmXmlPath = TestGeoJsonWriterPlugin.class.getClassLoader().getResource("map.osm").getPath();
		String rootPath = osmXmlPath.substring(0, osmXmlPath.lastIndexOf('/'));
		String directoryForCache = rootPath + "/cache";
		String geoJsonFile = rootPath + "/map.json";
		cleanDirectory(directoryForCache);
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(geoJsonFile)))) {
			new FileFeatureWriter(writer).register();
			Osmosis.run(new String[] {
					"-plugin",
					"com.chandan.osmosis.plugin.geojson.GeoJsonPluginLoader",
					"--read-xml",
					osmXmlPath,
					"--tag-filter", "accept-ways", "highway=*",
					"--tag-filter", "accept-relations", "highway=*",
					"--used-node",
					"--geojson-plugin",
					"geojson-writer=file-writer",
					"directoryForCache=" + directoryForCache
			});
		}
	}

	@Test
	public void testGeoJsonWriterForLalbagh() throws Exception {
		String osmXmlPath = TestGeoJsonWriterPlugin.class.getClassLoader().getResource("lalbagh.osm").getPath();
		String rootPath = osmXmlPath.substring(0, osmXmlPath.lastIndexOf('/'));
		String directoryForCache = rootPath + "/cache";
		String geoJsonFile = rootPath + "/lalbagh.json";
		cleanDirectory(directoryForCache);
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(geoJsonFile)))) {
			new FileFeatureWriter(writer).register();
			Osmosis.run(new String[] {
					"-plugin",
					"com.chandan.osmosis.plugin.geojson.GeoJsonPluginLoader",
					"--rx", osmXmlPath,
					"--tf", "accept-relations", "highway=*", "natural=water",
					"--used-way",
					"--used-node", //"idTrackerType=Dynamic",
					"outPipe.0=MP",

					"--rx", osmXmlPath,
					"--tf", "reject-relations",
					"--tf", "accept-ways", "highway=*",
					"--used-node", //"idTrackerType=Dynamic",
					"outPipe.0=H",

					"--merge", "inPipe.0=MP", "inPipe.1=H",
					//"--wx", rootPath + "/lalbagh2.osm"
					"--geojson-plugin",
					"geojson-writer=file-writer",
					"directoryForCache=" + directoryForCache
			});
		}
	}

	@Test
	public void testGeoJsonRelationsForLalbagh() throws Exception {
		String osmXmlPath = TestGeoJsonWriterPlugin.class.getClassLoader().getResource("lalbagh.osm").getPath();
		String rootPath = osmXmlPath.substring(0, osmXmlPath.lastIndexOf('/'));
		String directoryForCache = rootPath + "/cache";
		String geoJsonFile = rootPath + "/lalbagh.json";
		cleanDirectory(directoryForCache);
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(geoJsonFile)))) {
			new FileFeatureWriter(writer).register();
			Osmosis.run(new String[] {
					"-plugin",
					"com.chandan.osmosis.plugin.geojson.GeoJsonPluginLoader",
					"--rx", osmXmlPath,
					"--tf", "accept-relations", "highway=*",
					"--used-way","--used-node",
					"--geojson-plugin",
					"geojson-writer=file-writer",
					"directoryForCache=" + directoryForCache

			});
		}
	}

	private void cleanDirectory(String directory) {
		if (!new File(directory).exists()) {
			return;
		}
		try {
			FileUtils.cleanDirectory(new File(directory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
