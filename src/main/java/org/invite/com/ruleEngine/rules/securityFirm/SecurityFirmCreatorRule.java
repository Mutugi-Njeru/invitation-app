package org.invite.com.ruleEngine.rules.securityFirm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.SecurityFirm;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.SecurityFirmService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class SecurityFirmCreatorRule implements ServiceRule {
    @Inject
    SecurityFirmService securityFirmService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_SECURITY_FIRM.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        SecurityFirm securityFirm=new SecurityFirm(request);

        List<String> violations = beanValidatorService.validateDTO(securityFirm);
        if (violations.isEmpty()){
            ServiceResponder response=securityFirmService.createSecurityFirm(securityFirm);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.FIRM_CREATED.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), violations);
    }
}
