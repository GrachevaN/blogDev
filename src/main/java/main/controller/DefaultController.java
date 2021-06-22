package main.controller;

import main.api.response.InitResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController {


    @RequestMapping("/")
    public String index(Model model) {
//        model.addAttribute();
        return "index";
    }

//    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST}, value = "/**/{path}")
//    public String redirectToIndex() {
//        return "forward:/";
//    }

}
