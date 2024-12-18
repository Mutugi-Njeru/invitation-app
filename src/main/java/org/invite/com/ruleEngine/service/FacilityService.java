package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.FacilityDao;
import org.invite.com.model.Facility;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class FacilityService {
    @Inject
    FacilityDao facilityDao;

    public ServiceResponder createFacility(Facility facility){
        boolean isExist= facilityDao.isFacilityExist(facility.category());

        if (!isExist){
            int facilityId= facilityDao.createFacility(facility);
            return (facilityId>0)
                    ? new ServiceResponder(true, "facility created successfully")
                    : new ServiceResponder(false, "failed to create facility");
        }
        else return new ServiceResponder(false, "facility with that name already exists");
    }
    public ServiceResponder getFacilityCategories(JsonObject object){
        int structureId=object.getInt("structureId", 0);
        JsonObject categories=facilityDao.getFacilityCategories(structureId);
        return (!categories.isEmpty())
                ? new ServiceResponder(true, categories)
                : new ServiceResponder(false, "cannot get categories");
    }
}
