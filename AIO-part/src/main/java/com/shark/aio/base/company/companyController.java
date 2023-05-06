package com.shark.aio.base.company;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lbx
 * @date 2023/4/22 - 17:35
 **/
@Slf4j
@Controller
public class companyController {


    @RequestMapping("/company")
    public String company(HttpServletRequest req){


            return "company";
    }
}
