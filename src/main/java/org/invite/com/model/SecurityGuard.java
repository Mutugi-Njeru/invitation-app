package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.invite.com.utility.Constants;

public record SecurityGuard(
        @NotNull(message = "securityFirm cannot be empty")
        int securityFirmId,
        @NotEmpty(message = "name cannot be empty")
        String name,
        @NotNull(message = "guardNumber cannot be empty")
        int guardNumber,

        @NotEmpty(message = "msisdn cannot be empty")
        String msisdn,
        @NotEmpty(message = "email cannot be empty")
         @Email(message = "please provide a valid email")
        String email)
{
    public SecurityGuard(JsonObject object){
        this(
                object.getInt("securityFirmId", 0),
                object.getString("name", Constants.EMPTY_STRING),
                object.getInt("guardNumber", 0),
                object.getString("msisdn", Constants.EMPTY_STRING),
                object.getString("email", Constants.EMPTY_STRING)
        );
    }
}
