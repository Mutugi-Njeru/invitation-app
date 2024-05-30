package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record User(
        @NotNull(message = "structureId cannot be empty")
        int structureId,
        @NotNull(message = "userCategoryId cannot be empty")
        int userCategoryId,
        @NotEmpty(message = "username cannot be empty")
        String username,
        @NotEmpty(message = "password cannot be empty")
        String password,
        @NotEmpty(message = "firstname cannot be empty")
        String firstname,
        @NotEmpty(message = "lastname cannot be empty")
        String lastname,
        @NotEmpty(message = "msisdn cannot be empty")
        @Size(min = 12, message = "msisdn should have 12 digits")
        String msisdn,
        @NotEmpty(message = "email cannot be empty")
        @Email(message = "please provide a valid email")
        String email)
{
    public User(JsonObject object){
        this(
                object.getInt("structureId", 0),
                object.getInt("userCategoryId", 0),
                object.getString("username", ""),
                object.getString("password", ""),
                object.getString("firstname", ""),
                object.getString("lastname", ""),
                object.getString("msisdn", ""),
                object.getString("email", "")
        );
    }

}
