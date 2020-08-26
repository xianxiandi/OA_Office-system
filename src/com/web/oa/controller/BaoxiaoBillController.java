package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaoxiaoBillController {

    @RequestMapping("/main")
    public String main(ModelMap model){
      ActiveUser activeUser= (ActiveUser)SecurityUtils.getSubject().getPrincipal();
      model.addAttribute("activeUser",activeUser);
      return "index";
    }
}
