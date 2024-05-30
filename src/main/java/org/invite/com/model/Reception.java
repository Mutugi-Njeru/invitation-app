package org.invite.com.model;

import jakarta.json.JsonObject;
import org.invite.com.utility.Constants;

public record Reception(
        int firmId,
        String firstName,
        String lastName,
        String msisdn,
        String email)
{
    public Reception (JsonObject object){
        this(
                object.getInt("firmId", 0),
                object.getString("firstName", Constants.EMPTY_STRING),
                object.getString("lastName", Constants.EMPTY_STRING),
                object.getString("msisdn", Constants.EMPTY_STRING),
                object.getString("email", Constants.EMPTY_STRING)
        );
    }
}
