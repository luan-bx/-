package com.shark.aio.base.controller;

import com.shark.aio.user.entity.UserEntity;
import com.shark.aio.data.video.service.VideoRecorderService;
import com.shark.aio.user.mapper.UserMapping;
import com.shark.aio.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class BaseController {

	@Autowired
	private UserMapping userMapping;

	@RequestMapping("/")
	public String AIO(HttpServletRequest req) {
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
			return Constants.LOGIN;
		}
////		/**
////		 * 根据用户名，查询权限，返回对应首页
////		 */
		UserEntity userEntity = userMapping.queryUserByUserName(userName);
////		return authController.auth(userEntity, userName, req, response);
////		return "wastedWater";
		req.getSession().setAttribute("iconPath",userEntity.getIcon());
		req.getSession().setAttribute("userName",userEntity.getUserName());
		req.getSession().setMaxInactiveInterval(0);

		log.info("用户：" + userEntity.getUserName() + "登陆系统");
		return "index";

	}

}
