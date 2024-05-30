package org.invite.com.ruleEngine.rules.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.AuthService;
import org.invite.com.utility.Util;

import java.io.StringReader;

@ApplicationScoped
public class AuthRule implements ServiceRule {
    @Inject
    AuthService authService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.AUTHENTICATE.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject object = Json.createReader(new StringReader(input.toString())).readObject();
        ServiceResponder response=authService.authenticateUser(object);

        return (response.isSuccess())
                ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.AUTHENTICATION_SUCCESSFUL.name(), response.message())
                : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.AUTHENTICATION_FAILED.name(), response.message());
    }
}
