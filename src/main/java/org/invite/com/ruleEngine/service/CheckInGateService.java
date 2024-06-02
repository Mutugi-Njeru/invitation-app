package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.CheckInGateDao;
import org.invite.com.record.ServiceResponder;
import org.invite.com.utility.Constants;

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

    public ServiceResponder updateGateTimeOut(JsonObject object){
        String timeOut=object.getString("timeOut", Constants.EMPTY_STRING);
        int outId=object.getInt("outId", 0);
        boolean isUpdated= checkInGateDao.updateGateTimeOut(timeOut, outId);
        return (isUpdated)
                ? new ServiceResponder(true, "time out the gate updated successfully")
                : new ServiceResponder(false, "cannot update time out");
    }
}
