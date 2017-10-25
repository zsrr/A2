package com.stephen.a2.authorization;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.UUID;

public class TokenManagerImpl implements TokenManager {
    private final JedisPool jedisPool;

    private final String suffix;

    private final int lastTime;

    public TokenManagerImpl(JedisPool jedisPool, String suffix, int lastTime) {
        this.jedisPool = jedisPool;
        this.suffix = suffix;
        this.lastTime = lastTime;
    }

    private String getTokenKey(Serializable userId) {
        return userId + suffix;
    }

    @Override
    public TokenModel createToken(Serializable userId) {
        try(Jedis jedis = jedisPool.getResource()) {
            final String token = userId + "-" + UUID.randomUUID().toString().replace("-", "");
            jedis.set(getTokenKey(userId), token);
            jedis.expire(getTokenKey(userId), lastTime);
            return new TokenModel(userId, token);
        }
    }

    @Override
    public boolean checkToken(TokenModel tokenModel) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (tokenModel == null ||
                    tokenModel.getUserId() == null ||
                    tokenModel.getToken() == null) {
                return false;
            }
            String token = jedis.get(getTokenKey(tokenModel.getUserId()));
            if (token == null || !token.equals(tokenModel.getToken())) {
                return false;
            }
            jedis.expire(getTokenKey(tokenModel.getUserId()), lastTime);
            return true;
        }
    }

    @Override
    public TokenModel getToken(Serializable userId) {
        try(Jedis jedis = jedisPool.getResource()) {
            if (userId == null)
                return null;
            String token = jedis.get(getTokenKey(userId));

            if (token == null)
                return null;

            return new TokenModel(userId, token);
        }
    }

    @Override
    public TokenModel getToken(String token) {
        // 统一检查token不正确性
        try {
            String[] parts = token.split("-");
            Long userId = Long.parseLong(parts[0]);
            return new TokenModel(userId, token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteToken(Serializable userId) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(getTokenKey(userId));
        }
    }
}
