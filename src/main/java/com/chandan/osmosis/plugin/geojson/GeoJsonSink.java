package com.chandan.osmosis.plugin.geojson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import com.chandan.osmosis.plugin.geojson.processor.OsmNodeProcessor;
import com.chandan.osmosis.plugin.geojson.processor.OsmWayProcessor;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import org.apache.commons.io.FileUtils;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import org.openstreetmap.osmosis.core.task.v0_6.Source;

public class GeoJsonSink implements Sink {

	private FeaturePointCache pointCache;
	private FeatureLinestringCache lineStringCache;
	private final String directoryForCache;
	private FeatureWriter featureWriter;
	private OsmNodeProcessor osmNodeProcessor;
	private OsmWayProcessor osmWayProcessor;

	public GeoJsonSink(FeatureWriter featureWriter, String directoryForCache) {
		this.featureWriter = featureWriter;
		this.directoryForCache = directoryForCache;
	}

	@Override
	public void initialize(Map<String, Object> metaData) {
		try {
			FileUtils.deleteDirectory(new File(directoryForCache));
		}
		catch (IOException e) {
			throw new OsmosisRuntimeException(e);
		}
		featureWriter.open();
		new File(directoryForCache).mkdirs();
		pointCache = new FeaturePointCache(directoryForCache);
		pointCache.open();
		lineStringCache = new FeatureLinestringCache(directoryForCache);
		lineStringCache.open();
		this.osmNodeProcessor = new OsmNodeProcessor(pointCache, featureWriter);
		this.osmWayProcessor = new OsmWayProcessor(pointCache, lineStringCache, featureWriter);
		System.out.println("GeoJsonPlugin initialised");
	}

	@Override
	public void complete() {
	}

	@Override
	public void release() {
		try {
			featureWriter.close();
			pointCache.close();
			lineStringCache.close();
		} catch (Exception e) {
			throw new OsmosisRuntimeException(e);
		}
		System.out.println("GeoJsonPlugin released");
	}

	@Override
	public void process(EntityContainer entityContainer) {
		Entity entity = entityContainer.getEntity();
		EntityType entityType = entity.getType();
		switch (entityType) {
		case Node:
			Node node = (Node) entity;
			osmNodeProcessor.process(node);
			break;
		case Way:
			Way way = (Way) entity;
			osmWayProcessor.process(way);
			break;
		case Relation:
			Relation relation = (Relation) entity;
			break;
		}
	}
}
