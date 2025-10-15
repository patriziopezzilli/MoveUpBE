package com.moveup.repository;

import com.moveup.model.OutdoorLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutdoorLocationRepository extends MongoRepository<OutdoorLocation, String> {
    
    List<OutdoorLocation> findByLocationNear(GeoJsonPoint point, double maxDistance);
    
    List<OutdoorLocation> findByType(String type);
}
