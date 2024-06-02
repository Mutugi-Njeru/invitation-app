package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.FirmDao;
import org.invite.com.model.Firm;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class FirmService {
    @Inject
    FirmDao firmDao;

    public ServiceResponder createFirm(Firm firm){
        boolean isExist= firmDao.isFirmExist(firm.businessName(), firm.registrationPin());
        if (!isExist){
            int firmId= firmDao.createFirm(firm);
            int contactId=firmDao.saveFirmContacts(firmId, firm);
            return (firmId>0 && contactId>0)
                    ? new ServiceResponder(true, "firm created successfully")
                    : new ServiceResponder(false, "cannot create firm");
        }
        else return new ServiceResponder(false, "firm already exists");
    }
    public ServiceResponder getFirmsInAStructure(JsonObject object){
        int structureId= object.getInt("structureId", 0);
        JsonObject firms=firmDao.getFirmsInAStructure(structureId);
        return (!firms.isEmpty())
                ? new ServiceResponder(true, firms)
                : new ServiceResponder(false, "cannot get firms");
    }
    public ServiceResponder deactivateFirm(JsonObject object){
        int firmId= object.getInt("firmId", 0);
        boolean isDeactivated= firmDao.deactivateFirm(firmId);
        return (isDeactivated)
                ? new ServiceResponder(true, "firm deactivated successfully")
                : new ServiceResponder(false, "cannot deactivate firm");
    }
}
