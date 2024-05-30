package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.ShiftDao;
import org.invite.com.model.Shift;
import org.invite.com.record.ServiceResponder;
import org.invite.com.utility.Util;

@ApplicationScoped
public class ShiftService {
    @Inject
    ShiftDao shiftDao;

    public ServiceResponder addShift(Shift shift){
        boolean isTimeOkay= Util.isEndTimeAfterStartTime(shift.startTime(), shift.endTIme());
        if (isTimeOkay){
            int shiftId=shiftDao.createShift(shift);
            return (shiftId>0)
                    ? new ServiceResponder(true, "shift added successfully")
                    : new ServiceResponder(false, "cannot add shift");
        }
        else return new ServiceResponder(false, "endTime cannot be less than startTime");
    }
}
