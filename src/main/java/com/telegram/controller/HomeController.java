package com.telegram.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HomeController {

    @RequestMapping("/")
    public String home(HttpServletRequest req) {
        String requestUrl = getUrl(req);
        return "";
    }

    private String getUrl(HttpServletRequest req) {
        return req.getScheme() + "://" + req.getServerName() + ((req.getServerPort() == 80 || req.getServerPort() == 443) ? "" : ":" + req.getServerPort());
    }
}
