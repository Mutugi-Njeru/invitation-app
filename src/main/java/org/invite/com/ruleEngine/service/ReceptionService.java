package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.ReceptionDao;
import org.invite.com.model.Reception;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class ReceptionService {
    @Inject
    ReceptionDao receptionDao;

    public ServiceResponder createReception(Reception reception){
        int receptionId= receptionDao.addReception(reception);
        return (receptionId>0)
                ? new ServiceResponder(true, "reception added successfully")
                : new ServiceResponder(false, "cannot create reception");
    }
}
