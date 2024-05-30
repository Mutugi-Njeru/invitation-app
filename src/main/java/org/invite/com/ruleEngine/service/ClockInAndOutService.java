package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.ClockInAndOutDao;
import org.invite.com.model.ClockInAndOut;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class ClockInAndOutService {
    @Inject
    ClockInAndOutDao clockInAndOutDao;


    public ServiceResponder signInGuard(ClockInAndOut clockInAndOut){
        int signInId= clockInAndOutDao.signInGuardForShift(clockInAndOut);
        return (signInId>0)
                ? new ServiceResponder(true, "guard signedIn successfully")
                : new ServiceResponder(false, "cannot signIn guard");
    }
    public ServiceResponder signOutGuard(JsonObject object){
        int clockId= object.getInt("clockId", 0);
        String leaveTime=object.getString("leaveTime");
        boolean isSignedOut= clockInAndOutDao.signOutGuardForShift(clockId, leaveTime);
        return (isSignedOut)
                ? new ServiceResponder(true, "guard signed out successfully")
                : new ServiceResponder(false, "cannot sign out guard");
    }
}
