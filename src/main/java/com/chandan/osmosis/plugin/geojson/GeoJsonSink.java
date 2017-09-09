package com.chandan.osmosis.plugin.geojson;

import com.chandan.geojson.model.*;
import com.chandan.osmosis.plugin.geojson.cache.FeatureLinestringCache;
import com.chandan.osmosis.plugin.geojson.cache.FeatureMultiPolygonCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePointCache;
import com.chandan.osmosis.plugin.geojson.cache.FeaturePolygonCache;
import com.chandan.osmosis.plugin.geojson.common.Utils;
import com.chandan.osmosis.plugin.geojson.processor.OsmNodeProcessor;
import com.chandan.osmosis.plugin.geojson.processor.OsmRelationProcessor;
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
import java.util.List;
import java.util.Map;

public class GeoJsonSink implements Sink {

	private final String directoryForCache;

	private FeaturePointCache pointCache;

	private FeatureLinestringCache lineStringCache;

	private FeaturePolygonCache polygonCache;

	private FeatureMultiPolygonCache multiPolygonCache;

	private FeatureWriter featureWriter;

	private OsmNodeProcessor osmNodeProcessor;

	private OsmWayProcessor osmWayProcessor;

	private OsmRelationProcessor osmRelationProcessor;

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
		new File(directoryForCache).mkdirs();
		pointCache = new FeaturePointCache(directoryForCache);
		pointCache.open();
		lineStringCache = new FeatureLinestringCache(directoryForCache);
		lineStringCache.open();
		polygonCache = new FeaturePolygonCache(directoryForCache);
		polygonCache.open();

		multiPolygonCache = new FeatureMultiPolygonCache(directoryForCache);
		multiPolygonCache.open();

		this.osmNodeProcessor = new OsmNodeProcessor(pointCache);
		this.osmWayProcessor = new OsmWayProcessor(pointCache, lineStringCache, polygonCache);
		this.osmRelationProcessor = new OsmRelationProcessor(polygonCache, lineStringCache, multiPolygonCache);
		System.out.println("GeoJsonPlugin initialised");
	}

	@Override
	public void complete() {
		try {
			for (Feature<Point> feature : pointCache.getValues()) {
				if (!Utils.isMarkedDeleted(feature)) {
					featureWriter.write(feature);
				}
			}

			for (Feature<LineString> feature : lineStringCache.getValues()) {
				if (!Utils.isMarkedDeleted(feature)) {
					featureWriter.write(feature);
				}
			}

			for (Feature<Polygon> feature : polygonCache.getValues()) {
				if (!Utils.isMarkedDeleted(feature)) {
					featureWriter.write(feature);
				}
			}

			for (Feature<MultiPolygon> feature : multiPolygonCache.getValues()) {
				if (!Utils.isMarkedDeleted(feature)) {
					featureWriter.write(feature);
				}
			}
		} catch (Exception e) {
			throw new OsmosisRuntimeException(e);
		}
		System.out.println("GeoJsonPlugin completed");
	}

	@Override
	public void release() {
		try {
			featureWriter.complete();
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
			Feature<Polygon> feature = new Feature<>(0, polygon, null, boundingBox);
			featureWriter.write(feature);
			break;
		case Node:
			Node node = (Node) entity;
			try {
				osmNodeProcessor.process(node);
			} catch (Exception e) {
				throw new OsmosisRuntimeException(e);
			}
			break;
		case Way:
			Way way = (Way) entity;
			try {
				osmWayProcessor.process(way);
			} catch (Exception e) {
				throw new OsmosisRuntimeException(e);
			}
			break;
		case Relation:
			Relation relation = (Relation) entity;
			try {
				osmRelationProcessor.process(relation);
			} catch (Exception e) {
				throw new OsmosisRuntimeException(e);
			}
			break;
		}
	}
}
