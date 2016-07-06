package com.chandan.osmosis.plugin.geojson.converter;

import com.chandan.geojson.model.Geometry;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;

/**
 * Created by chandan on 05/07/16.
 */
public final class FeaturePropertyBuilderRegistry {

    private final Table<Class<? extends Entity>, Class<? extends Geometry>,
            FeaturePropertyBuilder<? extends Entity, ? extends Geometry>> table = HashBasedTable.create();

    private static final FeaturePropertyBuilderRegistry INSTANCE = new FeaturePropertyBuilderRegistry();

    private FeaturePropertyBuilderRegistry() {
    }

    public static FeaturePropertyBuilderRegistry instance() {
        return INSTANCE;
    }

    public <T extends Entity,U extends Geometry> void addPropertyBuilder(Class<T> osmEntity,
                                   Class<U> geojsonGeometry,
                                   FeaturePropertyBuilder<T, U> propertyBuilder) {
        table.put(osmEntity, geojsonGeometry, propertyBuilder);
    }

    public <T extends Entity, U extends Geometry> FeaturePropertyBuilder<T, U> getPropertyBuilder(Class<T> osmEntity, Class<U> geojsonGeometry) {
        return (FeaturePropertyBuilder<T, U>) table.get(osmEntity, geojsonGeometry);
    }
}
