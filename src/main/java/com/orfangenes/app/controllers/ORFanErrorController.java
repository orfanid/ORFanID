package com.orfangenes.app.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Suresh Hewapathirana
 */
//@Controller
public class ORFanErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }

//    @Override
    public String getErrorPath() {
        return "/error";
    }
}
