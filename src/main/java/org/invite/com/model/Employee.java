package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.invite.com.utility.Constants;

public record Employee(
        @NotNull(message = "firmId cannot be null")
        int firmId,
        @NotNull(message = "employeeNumber cannot be null")
        int employeeNumber,
        @NotEmpty(message = "title cannot be empty")
        String title,
        @NotEmpty(message = "firstName cannot be empty")
        String firstName,
        @NotEmpty(message = "lastName cannot be empty")
        String lastName,
        @NotEmpty(message = "msisdn cannot be empty")
        String msisdn,
        @NotNull(message = "idNumber cannot be null")
        int idNumber,
        @Email(message = "please put a valid email")
        @NotEmpty(message = "email cannot be blank")
        String email)
{
    public Employee(JsonObject object){
        this(
                object.getInt("firmId", 0),
                object.getInt("employeeNumber", 0),
                object.getString("title", Constants.EMPTY_STRING),
                object.getString("firstName", Constants.EMPTY_STRING),
                object.getString("lastName", Constants.EMPTY_STRING),
                object.getString("msisdn", Constants.EMPTY_STRING),
                object.getInt("idNumber", 0),
                object.getString("email", Constants.EMPTY_STRING)
        );
    }
}
