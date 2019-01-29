package com.orfangenes.controllers;

import com.orfangenes.model.entities.InputSequence;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
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
    public String result() {
        return "result";
    }

    @RequestMapping("/results")
    public String results() {
        return "results";
    }

    @RequestMapping("/instructions")
    public String instructions() {
        return "instructions";
    }
}
