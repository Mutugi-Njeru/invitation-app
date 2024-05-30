package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.invite.com.utility.Constants;

public record Firm(
        @NotNull(message = "categoryId cannot be empty")
        int categoryId,
        @NotNull(message = "SecurityFirmId cannot be empty")
        int securityFirmId,
        @NotEmpty(message = "businessName cannot be empty")
        String businessName,
        @NotEmpty(message = "businessNature cannot be empty")
        String businessNature,
        @NotEmpty(message = "registration pin cannot be empty")
        String registrationPin,
        @Email(message = "please provide a valid email")
        @NotEmpty(message = "email cannot be empty")
        String email,
        @NotEmpty(message = "msisdn cannot be empty")
        @Size(min = 12, message = "msisdn must have 12 digits")
        String primaryMsisdn,
        @NotEmpty(message = "msisdn cannot be empty")
        @Size(min = 12, message = "msisdn must have 12 digits")
        String secondaryMsisdn,
        @NotEmpty(message = "postalAddress cannot be empty")
        String postalAddress
) {
    public Firm(JsonObject object){
        this(
                object.getInt("categoryId", 0),
                object.getInt("securityFirmId", 0),
                object.getString("businessName", Constants.EMPTY_STRING),
                object.getString("businessNature", Constants.EMPTY_STRING),
                object.getString("registrationPin", Constants.EMPTY_STRING),
                object.getString("email", Constants.EMPTY_STRING),
                object.getString("primaryMsisdn", Constants.EMPTY_STRING),
                object.getString("secondaryMsisdn", Constants.EMPTY_STRING),
                object.getString("postalAddress", Constants.EMPTY_STRING)
        );
    }
}
