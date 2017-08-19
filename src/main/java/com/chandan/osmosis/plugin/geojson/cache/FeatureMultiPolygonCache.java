package com.chandan.osmosis.plugin.geojson.cache;

import com.chandan.geojson.model.Feature;
import com.chandan.geojson.model.MultiPolygon;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

/**
 * Created by chandanmadhesia on 15/08/17.
 */
public class FeatureMultiPolygonCache extends Cache<MultiPolygon> {

    private TypeReference<Feature<MultiPolygon>> typeReference = new TypeReference<Feature<MultiPolygon>>() {
        @Override
        public Type getType() {
            return super.getType();
        }
    };

    public FeatureMultiPolygonCache(String pathToDir) {
        super(pathToDir, "featureMultipolygonDb");
    }

    @Override
    public TypeReference getTypeReference() {
        return typeReference;
    }
}
