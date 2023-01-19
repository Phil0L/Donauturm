package com.pl.donauturm.calendar.api;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CalendarRequest extends Request<Void, String>{

    private final String username;
    private final String password;

    public CalendarRequest(String username, String password){
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.GET;
    }

    @Override
    protected String requestPath() {
        return "remote.php/dav/calendars/" + username + "/donauturm_shared_by_Donauturm/?export";
    }

    @Override
    public Headers headers() {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", getBasicAuthenticationHeader(username, password));
        return Headers.of(map);
    }

    @Override
    protected Class<? extends String> provideResultClass(String body) {
        return null;
    }

    private String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Override
    protected String parseResult(ResponseBody response) {
        try {
            String responseBody = response.string();
            logIncoming(responseBody);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void logIncoming(String body) {
        System.out.println("<- .ics file with " + body.split("\r\n|\r|\n").length);
    }
}
