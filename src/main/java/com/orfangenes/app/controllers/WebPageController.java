package com.orfangenes.app.controllers;

import com.orfangenes.app.model.InputSequence;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

//@Controller
public class WebPageController {

    @RequestMapping("/")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping("/input")
    public ModelAndView input() {
        return new ModelAndView("input", "sequence", new InputSequence());
    }

    @RequestMapping("/result")
    public ModelAndView result(@RequestParam(value = "sessionid") String sessionid) {
        ModelMap model = new ModelMap();
        model.addAttribute("sessionid", sessionid);
        return new ModelAndView("result",model);
    }

    @RequestMapping("/orfanbase")
    public String orfanbase() {
        return "orfanbase";
    }

    @RequestMapping("/results")
    public String results() {
        return "results";
    }

    @RequestMapping("/clamp")
    public String clamp() {
        return "clamp";
    }

    @RequestMapping("/clampresults")
    public String clampresults() {
        return "clampresults";
    }

    @RequestMapping("/instructions")
    public String instructions() {
        return "instructions";
    }
}
