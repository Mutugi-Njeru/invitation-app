package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.invite.com.utility.Constants;

public record SecurityFirm(
        @NotNull(message = "structureId cannot be empty")
        int structureId,
        @NotEmpty(message = "firmName cannot be empty")
        String firmName,
        @NotEmpty(message = "location cannot be empty")
        String location,
        @NotEmpty(message = "email cannot be empty")
        @Email
        String email,
        @NotEmpty(message = "primaryMsisdn cannot be empty")
        @Size(min = 12, message = "msisdn should have 12 digits")
        String primaryMsisdn,
        @NotEmpty(message = "secondaryMsisdn cannot be empty")
        @Size(min = 12, message = "msisdn should have 12 digits")
        String secondaryMsisdn)
{
    public SecurityFirm(JsonObject object){
        this(
                object.getInt("structureId", 0),
                object.getString("firmName", Constants.EMPTY_STRING),
                object.getString("location", Constants.EMPTY_STRING),
                object.getString("email", Constants.EMPTY_STRING),
                object.getString("primaryMsisdn", Constants.EMPTY_STRING),
                object.getString("secondaryMsisdn", Constants.EMPTY_STRING)
        );
    }

}
