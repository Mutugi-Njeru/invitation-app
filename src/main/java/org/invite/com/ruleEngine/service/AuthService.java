package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.invite.com.dao.AuthenticationDao;
import org.invite.com.record.Authentication;
import org.invite.com.record.ServiceResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

@ApplicationScoped
public class AuthService {
    @Inject
    JwtService jwtService;
    @Inject
    AuthenticationDao authenticationDao;
    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    public ServiceResponder authenticateUser(JsonObject object) {
        String username = object.getString("username", "");
        String password = object.getString("password", "");
        Authentication authentication = authenticationDao.authenticateUser(username, password);

        if (authentication.isAuthenticated()) {
            String accessToken = generateAccessToken(authentication);

            JsonObject response = Json.createObjectBuilder()
                    .add("authentication", Json.createObjectBuilder()
                            .add("bearer", accessToken)
                            .add("type", "token")
                            .add("expiresIn", 3600))
                    .build();
            return new ServiceResponder(true, response);
        } else {
            return new ServiceResponder(false, "Sorry wrong username or password");
        }
    }

    private String generateAccessToken(Authentication authentication){
            String accessToken= Base64.getEncoder().encodeToString(jwtService.generateAccessToken(authentication).getBytes());
            return accessToken;
    }
}
