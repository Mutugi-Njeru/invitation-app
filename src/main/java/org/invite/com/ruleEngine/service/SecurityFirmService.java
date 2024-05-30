package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.SecurityFirmDao;
import org.invite.com.model.SecurityFirm;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class SecurityFirmService {
    @Inject
    SecurityFirmDao securityFirmDao;

    public ServiceResponder createSecurityFirm(SecurityFirm securityFirm){
        int securityFirmId= securityFirmDao.createSecurityFirm(securityFirm);

        return (securityFirmId>0)
                ? new ServiceResponder(true, "security firm created successfully")
                : new ServiceResponder(false, "cannot create security firm");
    }
}
