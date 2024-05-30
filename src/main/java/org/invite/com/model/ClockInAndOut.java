package org.invite.com.model;

import jakarta.json.JsonObject;

public record ClockInAndOut(
        int guardId,
        int shiftId)
{
    public ClockInAndOut(JsonObject object){
        this(
                object.getInt("guardId", 0),
                object.getInt("shiftId", 0)
        );
    }
}
