package com.alibaba.aie.dtos;

public class Token {

    private String token;
    private Long expiresAt;

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }

    public Token(String token, Long expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
