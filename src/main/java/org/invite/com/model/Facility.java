package org.invite.com.model;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record Facility (
        @NotNull(message = "structureId cannot be empty")
        int structureId,
        @NotEmpty(message = "category cannot be empty")
        String category,
        @NotNull(message = "numberOfFirms cannot be empty")
        int numberOfFirms)
{
    public Facility(JsonObject object){
        this(
                object.getInt("structureId", 0),
                object.getString("category", ""),
                object.getInt("numberOfFirms", 0)
        );

    }

}
