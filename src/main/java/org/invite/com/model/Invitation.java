package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.invite.com.utility.Constants;

public record Invitation(
        @NotNull(message = "employeeId cannot be null")
        int employeeId,
        @NotEmpty(message = "reasonForVisit cannot be empty")
        String reasonForVisit,
        @NotEmpty(message = "timeIn cannot be empty")
        String expectedTimeIn,
        @NotEmpty(message = "timeOut cannot be empty")
        String expectedTimeOut,
        @NotEmpty(message = "firstname cannot be empty")
        String firstname,
        @NotEmpty(message = "lastname cannot be empty")
        String lastname,
        @NotEmpty(message = "msisdn cannot be empty")
        @Size(min = 12, message = "msisdn must have 12 numbers")
        String msisdn,
        @NotEmpty(message = "email cannot be empty")
        @Email(message = "please put a valid email")
        String email)
{
    public Invitation(JsonObject object){
        this(
                object.getInt("employeeId", 0),
                object.getString("reasonForVisit", Constants.EMPTY_STRING),
                object.getString("expectedTimeIn", Constants.EMPTY_STRING),
                object.getString("expectedTimeOut", Constants.EMPTY_STRING),
                object.getString("firstname", Constants.EMPTY_STRING),
                object.getString("lastname", Constants.EMPTY_STRING),
                object.getString("msisdn", Constants.EMPTY_STRING),
                object.getString("email", Constants.EMPTY_STRING)
        );

    }
}
