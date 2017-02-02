package com.chandan.osmosis.plugin.geojson;

import com.chandan.geojson.model.BoundingBox;
import com.chandan.geojson.model.Coordinate;
import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.Polygon;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.processor.OsmNodeProcessor;
import com.chandan.osmosis.plugin.geojson.processor.OsmWayProcessor;
import com.chandan.osmosis.plugin.geojson.writer.FeatureWriter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeoJsonSink implements Sink {

	private final String directoryForCache;

	private FeaturePointCache pointCache;

	private FeatureLinestringCache lineStringCache;

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
		}
		catch (Exception e) {
			throw new OsmosisRuntimeException(e);
		}
		System.out.println("GeoJsonPlugin released");
	}

	@Override
	public void process(EntityContainer entityContainer) {
		Entity entity = entityContainer.getEntity();
		EntityType entityType = entity.getType();
		switch (entityType) {
		case Bound:
			Bound bound = (Bound) entity;
			List<List<Coordinate>> coordinates = ImmutableList.<List<Coordinate>>of(ImmutableList.of(
					new Coordinate((float) bound.getLeft(), (float) bound.getBottom()),
					new Coordinate((float) bound.getRight(), (float) bound.getBottom()),
					new Coordinate((float) bound.getRight(), (float) bound.getTop()),
					new Coordinate((float) bound.getLeft(), (float) bound.getTop()),
					new Coordinate((float) bound.getLeft(), (float) bound.getBottom())
			));
			Polygon polygon = new Polygon(coordinates);
			BoundingBox boundingBox = new BoundingBox((float) bound.getLeft(), (float) bound.getBottom(),
					(float) bound.getRight(), (float) bound.getTop());
			Feature<Polygon> feature = new Feature<>(null, polygon, null, boundingBox);
			featureWriter.write(feature);
			break;
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
