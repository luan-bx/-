package com.shark.base.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shark.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {
	@RequestMapping("/")
	public String EPI(HttpServletRequest req) {
		/*
		 * 有无cookie缓存 有：进首页 无：跳转登录页面   key: value;浏览器带过来的
		 */
		Cookie[] cookies = req.getCookies();
		boolean flag = false;
		String userName = null;
		if (cookies != null) {
			for (Cookie ck : cookies) {
				if (ck.getName().equals(Constants.COOKIEHEAD)) {
					flag = true;
					userName = ck.getValue();
					log.info("BaseController/pms, 本次登录用户:{}", ck.getValue());
					break;
				}
			}
		}
		if (!flag) {
			//return Constants.LOGIN;
		}
		/**
		 * 根据用户名，查询权限，返回对应首页
		 */
//		UserEntity userEntity = userMapping.queryUserByUserName(userName);
//		return authController.auth(userEntity, userName, req, response);
		return "wastedWater";
	}

}
