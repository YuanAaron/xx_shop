package cn.coderap.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/remote")
public class RemoteResourceController {

    @RequestMapping("/findAge")
    public String findAge(String name) {
        System.out.println(name);
        return "30";
    }
}
