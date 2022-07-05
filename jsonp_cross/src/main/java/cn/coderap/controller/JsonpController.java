package cn.coderap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JsonpController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
