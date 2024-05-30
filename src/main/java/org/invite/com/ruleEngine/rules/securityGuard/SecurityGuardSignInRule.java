package org.invite.com.ruleEngine.rules.securityGuard;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.ClockInAndOut;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.ClockInAndOutService;
import org.invite.com.utility.Util;

@ApplicationScoped
public class SecurityGuardSignInRule implements ServiceRule {
    @Inject
    ClockInAndOutService clockInAndOutService;

    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.SIGN_IN.name()));

    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        ClockInAndOut clockInAndOut=new ClockInAndOut(request);
        ServiceResponder response=clockInAndOutService.signInGuard(clockInAndOut);

        return (response.isSuccess())
                ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.SIGNED_IN.name(), response.message())
                : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
    }
}
