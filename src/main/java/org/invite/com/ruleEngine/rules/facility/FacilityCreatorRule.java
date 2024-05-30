package org.invite.com.ruleEngine.rules.facility;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.Facility;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.FacilityService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class FacilityCreatorRule implements ServiceRule {
    @Inject
    FacilityService facilityService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_FACILITY.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        Facility facility=new Facility(request);

        List<String> violations = beanValidatorService.validateDTO(facility);
        if (violations.isEmpty()){
            ServiceResponder response=facilityService.createFacility(facility);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.REQUEST_SUCCESSFUL.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), violations);
    }
}
