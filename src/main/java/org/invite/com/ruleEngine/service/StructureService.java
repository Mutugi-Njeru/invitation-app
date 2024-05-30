package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.StructureDao;
import org.invite.com.model.Structure;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class StructureService {
    @Inject
    StructureDao structureDao;

    public ServiceResponder createStructure(Structure structure){
        boolean isExist= structureDao.isStructureExist(structure.premisesNumber());
        if (!isExist){
            int structureId= structureDao.createStructure(structure);
            return (structureId>0)
                    ? new ServiceResponder(true, "structure created successfully")
                    : new ServiceResponder(false, "cannot create structure");
        }
        else return new ServiceResponder(false, "structure already exists");
    }
}
