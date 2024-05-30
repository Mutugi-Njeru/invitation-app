package org.invite.com.controller;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.invite.com.enums.RequestTypes;
import org.invite.com.enums.ResultIds;
import org.invite.com.ruleEngine.engine.Engine;
import org.invite.com.utility.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("v1")
public class RequestController {
    @Inject
    Engine engine;

    @GET
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(InputStream inputstream, @HeaderParam("Authorization") String basicAuthHeader) throws IOException
    {

        var authArray = new String(Base64.getDecoder().decode(basicAuthHeader.replace("Basic", "").trim())).split(":");

        try
        {
            var request = Json.createObjectBuilder()
                    .add("username", authArray[0])
                    .add("password", authArray[1]);

            JsonObject response = engine.init(request.build(), RequestTypes.AUTHENTICATE.name(), "");

            return Response.status(Response.Status.OK)
                    .entity(response)
                    .build();
        }
        catch (Exception ex)
        {
            var response = Util.buildResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), ResultIds.AUTHENTICATION_FAILED.name(), "User verification failed");

            return Response.status(Response.Status.EXPECTATION_FAILED)
                    .entity(response)
                    .build();
        }

    }
    @POST
    @Path("request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processRequests(InputStream inputStream, @HeaderParam("Authorization") String bearerTokenHeader){
        try {
            String inputString=new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject object= Util.convertStringToJson(inputString);

            JsonObject request=object.containsKey("data") ? object.getJsonObject("data") : JsonValue.EMPTY_JSON_OBJECT;
            String requestType=object.getString("requestType", "");

            JsonObject response=engine.init(request, requestType, bearerTokenHeader);

            return Response.status(Response.Status.OK)
                    .entity(response.toString())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
