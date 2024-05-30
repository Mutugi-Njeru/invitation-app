package org.invite.com.ruleEngine.rules.checkInAndOut;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.CheckInGateService;
import org.invite.com.utility.Util;

@ApplicationScoped
public class ApproveGateEntryRule implements ServiceRule {
    @Inject
    CheckInGateService checkInGateService;

    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.APPROVE_ENTRY.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        ServiceResponder response=checkInGateService.checkInGate(request);
        return (response.isSuccess())
                ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.REQUEST_SUCCESSFUL.name(), response.message())
                : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
    }
}
