package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Structure(
        @NotEmpty(message = "name cannot be empty")
        String name,
        @NotNull(message = "premisesNumber cannot be empty")
        int premisesNumber,
        @NotEmpty(message = "location cannot be empty")
        String location,
        @NotEmpty(message = "address cannot be empty")
        String address,
        @NotEmpty(message = "email cannot be empty")
        String email,
        @NotNull(message = "numberOfFirms cannot be empty")
        int numberOfFirms,
        @NotEmpty(message = "primaryMsisdn cannot be empty")
        @Size(min = 12, message = "msisdn should have 12 digits")
        String primaryMsisdn,
        @NotEmpty(message = "secondaryMsisdn cannot be empty")
        @Size(min = 12, message = "msisdn should have 12 digits")
        String secondaryMsisdn)
{
    public Structure(JsonObject object){
        this(
                object.getString("name"),
                object.getInt("premisesNumber"),
                object.getString("location"),
                object.getString("address"),
                object.getString("email"),
                object.getInt("numberOfFirms"),
                object.getString("primaryMsisdn"),
                object.getString("secondaryMsisdn")
        );
    }
}
