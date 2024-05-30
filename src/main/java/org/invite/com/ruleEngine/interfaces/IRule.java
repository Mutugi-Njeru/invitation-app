package org.invite.com.ruleEngine.interfaces;

import jakarta.json.JsonObject;

public interface IRule <I>{
    boolean matches(I input);
    JsonObject apply(I input);
}
