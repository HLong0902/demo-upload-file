package com.sudin.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sudin Ranjitkar on 5/17/2017.
 */
@RestController
public class HomeController {

    @RequestMapping(value = "/")
    public String home() {
        return "hello!!";
    }

}
