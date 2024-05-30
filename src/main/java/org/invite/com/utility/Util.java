package org.invite.com.utility;

import jakarta.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.security.SecureRandom;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Util {
    private static final Logger logger= LoggerFactory.getLogger(Util.class);
    public Util(){}

    public static JsonObject buildResponse(int statusCode, String status, Object message)
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (message instanceof String)
        {
            builder.add("message", message.toString());
        }
        else if (message instanceof JsonStructure jsonStructure)
        {
            builder.add("message", jsonStructure);
        }
        else if (message instanceof List)
        {
            List<Object> list = (List<Object>) message;
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            for (Object item : list)
            {
                if (item instanceof JsonStructure jsonStructure)
                {
                    jsonArrayBuilder.add(jsonStructure);
                }
                else
                {
                    jsonArrayBuilder.add(item.toString());
                }
            }
            builder.add("message", jsonArrayBuilder.build());
        }
        else
        {
            builder.add("message", JsonValue.EMPTY_JSON_OBJECT); // Handle unsupported message type gracefully
        }
        return builder
                .add("statusCode", statusCode)
                .add("status", status)
                .build();
    }

    public static JsonObject convertStringToJson(String string)
    {
        try
        {
            return Json.createReader(new StringReader(string)).readObject();
        }
        catch (Exception ex)
        {
            logger.info(Constants.ERROR_LOG_TEMPLATE, ex.getClass().getSimpleName(), ex.getMessage());
            return JsonObject.EMPTY_JSON_OBJECT;
        }
    }
    public static String getTimestamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static String generateOtp()
    {
        final int OTP_LENGTH = 6;
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++)
        {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            otp.append(randomChar);
        }
        return otp.toString();
    }
    public static boolean isEndTimeAfterStartTime(String startTime, String endTime){
        Time startDate=Time.valueOf(startTime);
        Time endDate=Time.valueOf(endTime);

        if (endDate.after(startDate)){
            return true;
        }
        else return false;
    }
    public static boolean isTimeoutAfterTimeIn(String timeIn, String timeout){
        Timestamp startDate=Timestamp.valueOf(timeIn);
        Timestamp endDate=Timestamp.valueOf(timeout);

        if (endDate.after(startDate)){
            return true;
        }
        else return false;
    }

}
