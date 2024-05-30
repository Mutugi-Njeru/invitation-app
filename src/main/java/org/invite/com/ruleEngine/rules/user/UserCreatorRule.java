package org.invite.com.ruleEngine.rules.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.User;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.UserService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class UserCreatorRule implements ServiceRule {
    @Inject
    UserService userService;
    @Inject
    BeanValidatorService beanValidatorService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_USER.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        User user=new User(request);
        List<String> violations = beanValidatorService.validateDTO(user);

        if (violations.isEmpty()){
            ServiceResponder response=userService.createUser(user);

            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.USER_CREATED.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.USER_NOT_CREATED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.VALIDATION_FAILED.name(), violations);
    }
}
