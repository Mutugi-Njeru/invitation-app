package org.invite.com.ruleEngine.rules.firm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.Firm;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.FirmService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class FirmCreatorRule implements ServiceRule {
    @Inject
    FirmService firmService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_FIRM.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        Firm firm=new Firm(request);
        List<String> violations = beanValidatorService.validateDTO(firm);
        if (violations.isEmpty()){
            ServiceResponder response=firmService.createFirm(firm);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.FIRM_CREATED.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());

        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.VALIDATION_FAILED.name(), violations);

    }
}
