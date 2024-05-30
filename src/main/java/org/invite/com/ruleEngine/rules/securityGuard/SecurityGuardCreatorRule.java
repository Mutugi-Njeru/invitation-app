package org.invite.com.ruleEngine.rules.securityGuard;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.SecurityGuard;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.SecurityGuardService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class SecurityGuardCreatorRule implements ServiceRule {
    @Inject
    SecurityGuardService securityGuardService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_SECURITY_GUARD.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        SecurityGuard securityGuard=new SecurityGuard(request);
        List<String> violations = beanValidatorService.validateDTO(securityGuard);
        if (violations.isEmpty()){
            ServiceResponder response=securityGuardService.createSecurityGuard(securityGuard);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.REQUEST_SUCCESSFUL.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.VALIDATION_FAILED.name(), violations);
    }
}
