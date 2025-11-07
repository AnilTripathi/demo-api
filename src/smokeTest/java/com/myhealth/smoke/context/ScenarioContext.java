package com.myhealth.smoke.context;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    
    private static final ScenarioContext INSTANCE = new ScenarioContext();
    
    private final Map<String, Object> context = new HashMap<>();
    private Response lastResponse;
    private String accessToken;
    
    private ScenarioContext() {}
    
    public static ScenarioContext getInstance() {
        return INSTANCE;
    }
    
    public void setData(String key, Object value) {
        context.put(key, value);
    }
    
    public Object getData(String key) {
        return context.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type) {
        return (T) context.get(key);
    }
    
    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }
    
    public Response getLastResponse() {
        return lastResponse;
    }
    
    public void setAccessToken(String token) {
        this.accessToken = token;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void clear() {
        context.clear();
        lastResponse = null;
        accessToken = null;
    }
}