package com.sample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/hello")
    public String helloController() {
        return "<h1>Hello World</h1>";
    }

    @RequestMapping("/blogExp")
    public String blogController() {
        return "<h1>Fantasy Blog</h1>";
    }
}