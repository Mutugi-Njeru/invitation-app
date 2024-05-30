package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.FirmDao;
import org.invite.com.model.Firm;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class FirmService {
    @Inject
    FirmDao firmDao;
    //check if firm exists
    //create firm
    //save firm contacts

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
}
