package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.SecurityGuardDao;
import org.invite.com.model.SecurityGuard;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class SecurityGuardService {
    @Inject
    SecurityGuardDao securityGuardDao;

    public ServiceResponder createSecurityGuard(SecurityGuard securityGuard){
        int securityGuardId= securityGuardDao.createSecurityGuard(securityGuard);
        return (securityGuardId>0)
                ? new ServiceResponder(true, "security guard created successfully")
                : new ServiceResponder(false, "cannot create guard");
    }
}
