package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import org.invite.com.dao.EmployeeDao;
import org.invite.com.model.Employee;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class EmployeeService {
    @Inject
    EmployeeDao employeeDao;

    public ServiceResponder createEmployee(Employee employee){
        boolean isExist= employeeDao.isEmployeeExist(employee.employeeNumber(), employee.idNumber());
        if (!isExist){
            int employeeId= employeeDao.createEmployee(employee);
            return (employeeId>0)
                    ? new ServiceResponder(true, "employee created successfully")
                    : new ServiceResponder(false, "cannot create employee");
        }
        else return new ServiceResponder(false, "employee already exists");
    }

    public ServiceResponder grantInviteAccess(JsonObject object){
        int employeeId= object.getInt("employeeId", 0);
        boolean isGranted= employeeDao.grantInviteAccess(employeeId);
        return (isGranted)
                ? new ServiceResponder(true, "invite access granted successfully")
                : new ServiceResponder(false, "cannot grant invite access");
    }
    public ServiceResponder deactivateEmployee(JsonObject object){
        int employeeId= object.getInt("employeeId", 0);
        boolean isDeactivated= employeeDao.deactivateEmployee(employeeId);
        return (isDeactivated)
                ? new ServiceResponder(true, "employee deactivated successfully")
                : new ServiceResponder(false, "cannot deactivate employee");
    }
    public ServiceResponder getFirmEmployees(JsonObject object){
        int firmId= object.getInt("firmId");
        JsonObject employees=employeeDao.getFirmEmployees(firmId);
        return (!employees.isEmpty())
                ? new ServiceResponder(true, employees)
                : new ServiceResponder(false, "cannot get employees");
    }
    public ServiceResponder getFirmEmployeesWithInviteRules(JsonObject object){
        int firmId= object.getInt("firmId");
        JsonObject employees=employeeDao.getFirmEmployeesWithInviteRight(firmId);
        return (!employees.isEmpty())
                ? new ServiceResponder(true, employees)
                : new ServiceResponder(false, "cannot get employees");
    }
}
