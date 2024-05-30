package org.invite.com.ruleEngine.rules.structure;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.Structure;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.StructureService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class StructureCreatorRule implements ServiceRule {
    @Inject
    StructureService structureService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_STRUCTURE.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject object= Util.convertStringToJson(input.toString());
        Structure structure=new Structure(object);
        List<String> violations = beanValidatorService.validateDTO(structure);

        if (violations.isEmpty()){
            ServiceResponder response=structureService.createStructure(structure);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.REQUEST_SUCCESSFUL.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.VALIDATION_FAILED.name(), violations);
    }
}
