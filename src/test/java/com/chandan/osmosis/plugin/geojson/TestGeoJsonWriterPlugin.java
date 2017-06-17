package com.chandan.osmosis.plugin.geojson;

import org.junit.Test;
import org.openstreetmap.osmosis.core.Osmosis;

/**
 * Created by chandan on 5/4/16.
 */
public class TestGeoJsonWriterPlugin {

	@Test
	public void testGeoJsonWriter() {
		String osmXmlPath = TestGeoJsonWriterPlugin.class.getClassLoader().getResource("map.osm").getPath();
		String rootPath = osmXmlPath.substring(0, osmXmlPath.lastIndexOf('/'));
		String directoryForCache = rootPath + "/cache";
		String geoJsonFile = rootPath + "/map.json";
		System.out.println(geoJsonFile);
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
				"geojson-file=" + geoJsonFile,
				"directoryForCache=" + directoryForCache
		});
	}

	@Test
	public void testGeoJsonWriterForLalbagh() {
		String osmXmlPath = TestGeoJsonWriterPlugin.class.getClassLoader().getResource("lalbagh.osm").getPath();
		String rootPath = osmXmlPath.substring(0, osmXmlPath.lastIndexOf('/'));
		String directoryForCache = rootPath + "/cache";
		String geoJsonFile = rootPath + "/lalbagh.json";

		System.out.println(geoJsonFile);
		Osmosis.run(new String[] {
				"-plugin",
				"com.chandan.osmosis.plugin.geojson.GeoJsonPluginLoader",
				"--rx", osmXmlPath,
				"--tf", "accept-relations", "highway=*",
				"--used-node", "--used-way", "outPipe.0=MP",

				"--rx", osmXmlPath,
				"--tf", "accept-ways", "highway=*",
				"--used-node", "outPipe.0=H",

				"--merge", "inPipe.0=MP", "inPipe.1=H",
				"--geojson-plugin",
				"geojson-writer=file-writer",
				"geojson-file=" + geoJsonFile,
				"directoryForCache=" + directoryForCache
		});

	}
}
