package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexMvcController extends BaseAuthenticationController{

    public IndexMvcController(AuthenticationHelper authenticationHelper) {
        super(authenticationHelper);
    }

    @GetMapping
    public String showPanelPage() {
        return "index";
    }
}
