package com.stephen.a2.authorization;

import java.io.Serializable;

public interface TokenManager {
    TokenModel createToken(Serializable userId);
    boolean checkToken(TokenModel tokenModel);
    TokenModel getToken(Serializable userId);
    TokenModel getToken(String token);
    void deleteToken(Serializable userId);
}
