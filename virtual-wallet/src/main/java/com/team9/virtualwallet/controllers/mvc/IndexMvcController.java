package com.team9.virtualwallet.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexMvcController extends BaseAuthenticationController{

    @GetMapping
    public String showPanelPage() {
        return "index";
    }
}
