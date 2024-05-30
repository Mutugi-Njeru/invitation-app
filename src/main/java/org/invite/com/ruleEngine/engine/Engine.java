package org.invite.com.ruleEngine.engine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.record.Token;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.JwtService;
import org.invite.com.utility.Util;

@ApplicationScoped
public class Engine {
    @Inject
    JwtService jwtService;
    @Inject
    private Instance<ServiceRule> rules;

    public JsonObject init(JsonObject request, String requestType, String bearerToken){
        Token tokenDetails=authorizeUser(requestType, bearerToken);

        return (tokenDetails.isValid())
                ? start(request, requestType)
                : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), "Invalid Operation");

    }
    private Token authorizeUser(String requestType, String bearerToken){
        String token = (bearerToken.startsWith("Bearer"))
                ? bearerToken.replace("Bearer ", "").trim()
                : bearerToken;

        return (requestType.equalsIgnoreCase(RequestTypes.AUTHENTICATE.name()))
                ? new Token(true, "")
                : jwtService.decodeAccessToken(token);

    }
    private JsonObject start(JsonObject request, String requestType)
    {
        JsonObject response = JsonObject.EMPTY_JSON_OBJECT;
        boolean isStarted = false;

        for (ServiceRule rule : rules)
        {
            if (rule.matches(requestType))
            {
                isStarted = true;
                response = rule.apply(request);
                break;
            }
        }

        return (isStarted)
                ? response
                : Util.buildResponse((Response.Status.EXPECTATION_FAILED.getStatusCode()), ResultIds.REQUEST_FAILED.name(), "unknown request type");

    }
}
