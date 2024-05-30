package org.invite.com.ruleEngine.rules.employee;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.model.Employee;
import org.invite.com.record.ServiceResponder;
import org.invite.com.ruleEngine.interfaces.ServiceRule;
import org.invite.com.ruleEngine.service.BeanValidatorService;
import org.invite.com.ruleEngine.service.EmployeeService;
import org.invite.com.utility.Util;

import java.util.List;

@ApplicationScoped
public class EmployeeCreatorRule implements ServiceRule {
    @Inject
    BeanValidatorService beanValidatorService;
    @Inject
    EmployeeService employeeService;
    @Override
    public boolean matches(Object input) {
        return (input.toString().equalsIgnoreCase(RequestTypes.CREATE_EMPLOYEE.name()));
    }

    @Override
    public JsonObject apply(Object input) {
        JsonObject request= Util.convertStringToJson(input.toString());
        Employee employee=new Employee(request);
        List<String> violations = beanValidatorService.validateDTO(employee);

        if (violations.isEmpty()){
            ServiceResponder response=employeeService.createEmployee(employee);
            return (response.isSuccess())
                    ? Util.buildResponse(Response.Status.OK.getStatusCode(), ResultIds.EMPLOYEE_CREATED.name(), response.message())
                    : Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.REQUEST_FAILED.name(), response.message());
        }
        else return Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.VALIDATION_FAILED.name(), violations);
    }
}
