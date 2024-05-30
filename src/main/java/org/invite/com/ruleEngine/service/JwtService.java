package org.invite.com.ruleEngine.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import org.invite.com.record.Authentication;
import org.invite.com.record.Token;
import org.invite.com.utility.Util;

import java.util.Base64;
import java.util.Date;

@ApplicationScoped
public class JwtService {
    public String generateAccessToken(Authentication authentication){
        Long milliseconds=(new Date().getTime()) + (3600 * 1000);
        Date expireDate=new Date(milliseconds);

        return JWT.create()
                .withClaim("createdAt", Util.getTimestamp())
                .withClaim("username", authentication.username())
                .withIssuer("cashio")
                .withClaim("expiresIn", 3600)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC512("coder"));
    }

    public Token decodeAccessToken(String accessToken){
        byte[] decodeBytes= Base64.getDecoder().decode(accessToken.getBytes());
        accessToken=new String(decodeBytes);

        JWTVerifier verifier= JWT.require(Algorithm.HMAC512("coder"))
                .withIssuer("cashio")
                .acceptExpiresAt(3600)
                .build();
        DecodedJWT decodedJWT= verifier.verify(accessToken);
        String username=decodedJWT.getClaims().get("username").asString();
        return new Token(true, username);
    }
}
