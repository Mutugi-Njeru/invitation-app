package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.ApproveAndRejectInvitationDao;
import org.invite.com.dao.EmployeeDao;
import org.invite.com.dao.FirmDao;
import org.invite.com.dao.InvitationDao;
import org.invite.com.model.Invitation;
import org.invite.com.record.ServiceResponder;
import org.invite.com.utility.Constants;
import org.invite.com.utility.Util;

@ApplicationScoped
public class InvitationService {
    @Inject
    FirmDao firmDao;
    @Inject
    InvitationDao invitationDao;
    @Inject
    ApproveAndRejectInvitationDao approveAndRejectInvitationDao;
    @Inject
    EmployeeDao employeeDao;

    public ServiceResponder createInvitation(Invitation invitation){
        boolean isInvite= invitationDao.isInvite(invitation.employeeId());
        boolean isEmployeeActive= employeeDao.isEmployeeActive(invitation.employeeId());
        if (isInvite && isEmployeeActive){
            boolean isTimeOkay= Util.isTimeoutAfterTimeIn(invitation.expectedTimeIn(), invitation.expectedTimeOut());
            if (isTimeOkay){
                int firmId= firmDao.getFirmIdByEmployeeId(invitation.employeeId());

                if (firmId>0){
                    int visitorId= invitationDao.createVisitor(firmId, invitation);
                    if (visitorId>0){
                        int invitationId= invitationDao.createInvitation(visitorId, invitation);
                        return (invitationId>0)
                                ? new ServiceResponder(true, "invitation created successfully")
                                : new ServiceResponder(false, "cannot create invitation");
                    }
                    else return new ServiceResponder(false, "invalid visitorId");

                }
                else return new ServiceResponder(false, "invalid employeeId");
            }
            else return new ServiceResponder(false, "Expected time in cannot exceed expected time out");

        }
        else return new ServiceResponder(false, "you are not allowed to make invitations or inactive");
    }

    public ServiceResponder approveInvitation(JsonObject object){
        int invitationId= object.getInt("invitationId", 0);
        int receptionId= object.getInt("receptionId", 0);
        boolean isApproved= approveAndRejectInvitationDao.approveInvitation(invitationId, receptionId);
        if (isApproved){
            boolean isUpdated=approveAndRejectInvitationDao.updateInvitationStatusToApproved(invitationId);
            return (isUpdated)
                    ? new ServiceResponder(true, "invitation approved successfully")
                    : new ServiceResponder(false, "cannot approve invitation");
        }
        else return new ServiceResponder(false, "cannot approve invitation");
    }
    public ServiceResponder rejectInvitation(JsonObject object){
        int invitationId= object.getInt("invitationId", 0);
        int receptionId= object.getInt("receptionId", 0);
        boolean isDenied= approveAndRejectInvitationDao.rejectInvitation(invitationId, receptionId);
        if (isDenied){
            boolean isUpdated=approveAndRejectInvitationDao.updateInvitationStatusToRejected(invitationId);
            return (isUpdated)
                    ? new ServiceResponder(true, "invitation rejected successfully")
                    : new ServiceResponder(false, "cannot reject application");
        }
        else return new ServiceResponder(false, "cannot reject application");
    }
    public ServiceResponder updateVisitorTimeInTheFirm(JsonObject object){
        String timeIn= object.getString("timeIn", Constants.EMPTY_STRING);
        int approveId= object.getInt("approveId", 0);
        boolean isUpdated= approveAndRejectInvitationDao.updateVisitorTimeInTheFirm(timeIn, approveId);
        return (isUpdated)
                ? new ServiceResponder(true, "timeIn updated successfully")
                : new ServiceResponder(false, "cannot update time in");
    }
    //to add time in not to exceed time out

    public ServiceResponder updateVisitorTimeOutTheFirm(JsonObject object){
        String timeOut= object.getString("timeOut", Constants.EMPTY_STRING);
        int approveId= object.getInt("approveId", 0);
        boolean isUpdated= approveAndRejectInvitationDao.updateVisitorTimeOutTheFirm(timeOut, approveId);
        return (isUpdated)
                ? new ServiceResponder(true, "timeOut updated successfully")
                : new ServiceResponder(false, "cannot update time out");
    }
    public ServiceResponder getAllVisitors(JsonObject object){
        int firmId= object.getInt("firmId", 0);
        JsonObject visitors=invitationDao.getAllFirmVisitors(firmId);
        return (!visitors.isEmpty())
                ? new ServiceResponder(true, visitors)
                : new ServiceResponder(false, "cannot get firm visitors");
    }
    public ServiceResponder getInvitationDetailsByCode(JsonObject object){
        String invitationCode= object.getString("invitationCode", Constants.EMPTY_STRING);
        JsonObject invitation=invitationDao.getInvitationDetailsByCode(invitationCode);
        return (!invitation.isEmpty())
                ? new ServiceResponder(true, invitation)
                : new ServiceResponder(false, "invalid invitation code or code expired");
    }
    public ServiceResponder getApprovedInvitations(JsonObject object){
        int firmId= object.getInt("firmId", 0);
        JsonObject invitations=invitationDao.getApprovedInvitations(firmId);
        return (!invitations.isEmpty())
                ? new ServiceResponder(true, invitations)
                : new ServiceResponder(false, "cannot get approved invitations");

    }
}
