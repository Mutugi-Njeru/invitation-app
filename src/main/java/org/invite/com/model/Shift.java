package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotEmpty;
import org.invite.com.utility.Constants;

public record Shift(
        @NotEmpty(message = "shiftName cannot be empty")
        String shiftName,
        @NotEmpty(message = "startTime cannot be empty")
        String startTime,
        @NotEmpty(message = "endTime cannot be empty")
        String endTIme)
{
    public Shift(JsonObject object){
        this(
                object.getString("shiftName", Constants.EMPTY_STRING),
                object.getString("startTime", Constants.EMPTY_STRING),
                object.getString("endTIme", Constants.EMPTY_STRING)
        );
    }
}
