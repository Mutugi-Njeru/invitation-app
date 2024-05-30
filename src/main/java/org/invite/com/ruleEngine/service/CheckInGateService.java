package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.CheckInGateDao;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class CheckInGateService {
    @Inject
    CheckInGateDao checkInGateDao;

    public ServiceResponder checkInGate(JsonObject object){
        int clockId= object.getInt("clockInId", 0);
        int approvedId= object.getInt("approvedId", 0);
        boolean isGuardSignedIn= checkInGateDao.isGuardSignedIn(clockId);
        if (isGuardSignedIn){
            int entryId= checkInGateDao.approveGateEntry(clockId, approvedId);
            boolean updateApproved= checkInGateDao.updateApproveRejectInvitationToCheckedIn(approvedId);
            return (entryId>0 && updateApproved)
                    ? new ServiceResponder( true, "entry to the premises approved")
                    : new ServiceResponder(false, "cannot approve entry");
        }
        else return new ServiceResponder(false, "you must be signed in on active shift to approve entry");
    }
}
