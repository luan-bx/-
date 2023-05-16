package com.shark.aio.base.controller;

import com.shark.aio.base.information.InformationController;
import com.shark.aio.user.entity.UserEntity;
import com.shark.aio.user.mapper.UserMapping;
import com.shark.aio.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
public class BaseController {

	@Autowired
	private UserMapping userMapping;

	@Autowired
	InformationController informationController;

	@RequestMapping("/")
	public String AIO(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
					log.info("本次登录用户:{}", ck.getValue());
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

		try{
			UserEntity userEntity = userMapping.queryUserByUserName(userName);
			if(userEntity == null){
				return Constants.LOGIN;
			}
			req.getSession().setAttribute("iconPath",userEntity.getIcon());
			req.getSession().setAttribute("userName",userEntity.getUserName());

			req.getSession().setMaxInactiveInterval(0);
			log.info("用户：" + userEntity.getUserName() + "登陆系统");
		}catch (Exception e){
			return Constants.LOGIN;
		}



		return informationController.indexWeb(req);

	}

}
