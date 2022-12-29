package com.lbx.aio.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lbx.aio.util.ConfPhone;
import com.lbx.aio.util.Constants;
import com.lbx.aio.util.SendSms;
import com.lbx.aio.util.VerCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lbx.aio.user.entity.UserEntity;
import com.lbx.aio.user.mapper.UserMapping;
import com.lbx.aio.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {

	@Autowired
	protected UserService userService;
	@Autowired
	protected UserMapping userMapping;

	/*
	 * 20220908-thg，获得手机验证码
	 */
	@RequestMapping(value = "/getPhoneCode" , method = RequestMethod.POST , produces = "text/html; charset=UTF-8")
	@ResponseBody
	public String getPhoneCode(String phone,HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		/*
		 * response.setHeader("Content-type", "text/html;charset=UTF-8");
		 * response.setCharacterEncoding("UTF-8");
		 */
		try {
			if(ConfPhone.confPhone(phone)) {
				String code = VerCodeGenerateUtil.generateNumberCode();
				session.setMaxInactiveInterval(300);
				session.setAttribute("phone", phone);
				session.setAttribute("phonecode", code);
				
				SendSms.SendMessageCode(code, phone);
				log.info("已向手机号："+phone+"发送短信");
				return "发送验证码成功";
			}
			return "手机号不正确，请检查您输入的手机号。";
		} catch (Exception e) {
			log.info("发送验证码失败",e);
			
		}
		return "发送验证码失败";
	}
	
	/*
	 * 用户-密码注册
	 */
	@RequestMapping("/signup")
	public String signup(UserEntity userEntity, String code, HttpSession session, 
			@RequestParam(value = "file", required = false) List<MultipartFile> file, HttpServletRequest req) {
		/*
		 * 是否是高级管理员，string转boolean
		 */
//		if (isAdminInt == 1) {
//			userEntity.setAdmin(true);
//		} else {
//			userEntity.setAdmin(false);
//		}
		//20220908-thg,加入手机号正确验证
//				if(!ConfPhone.confPhone(userEntity.getPhone())) {
//					log.info("手机号错误");
//					req.setAttribute(Constants.ERROR, "手机号错误,请输入正确的手机号");
//					return regist(req);
//				}
//		//20220908-thg,验证码
//				if(!userEntity.getPhone().equals(session.getAttribute("phone"))) {
//					log.info("用户中途更换手机号码");
//					req.setAttribute(Constants.ERROR, "请勿中途更换手机号码");
//					return regist(req);
//				}
//				if(!code.equals(session.getAttribute("phonecode"))) {
//					log.info("验证码错误或过期");
//					req.setAttribute(Constants.ERROR, "验证码错误或过期");
//					return regist(req);
//				}
//		userEntity.setAdmin(false);
//20220908-thg		userEntity.setDepartmentId(userService.departmentId(userEntity.getDepartmentName()));
//		userEntity.setPostId(userService.postId(userEntity.getPostName()));
		userEntity.setPostId(16);
		userEntity.setPostName("待管理员审核");
		//20220908-thg,注册自动设置部门和邮箱
//		userEntity.setDepartmentId(5);
//		userEntity.setDepartmentName("未分配部门");
		userEntity.setEmail("未绑定邮箱");
		/*
		 * 如果是第一个注册用户，自动设置为管理员
		 */
		if(userEntity.getId() == 1) {
			userEntity.setPostId(1);
			userEntity.setPostName("管理员");
		}

		// 判断新用户(用户名-密码表)
		String isNewUser = userService.isNewUser(userEntity, req);
		if (isNewUser.equals(Constants.SUCCESSCODE)) {
			
			String addResult = userService.addUser(userEntity, file, req);
			if (addResult.equals(Constants.FAILCODE)) {
				log.info("注册失败");
//				return Constants.SIGNUP;
				return "redirect:/regist"; //qh 20220412
			}
			// 是新用户
			
			/*
			 *  判断是否绑定wx
			 */
//			String bindResult = UserService.bindUser(userEntity.getPhone());
//			if (!bindResult.equals(Constants.FAILCODE)) {
//				// 绑定成功
//				log.info("UserController/signup, 新用户微信绑定成功");
//			} else {
//				// 绑定失败(wx也未注册过)
//				log.info("UserController/signup, 新用户微信绑定失败");
//			}
			log.info("注册成功");
			return Constants.LOGIN;// 注册成功返回登录页面
		} else if (isNewUser.equals(Constants.ERROR)) {
			// 查询失败
			log.info("UserController/signup, 用户信息查询失败");
			return Constants.ERROR;
		} else {
			// 查询存在的,返回已经注册过
			log.info("已经注册过，请返回登录");
			return Constants.LOGIN;
		}
	}

	/*
	 * 用户-密码登录
	 */
	@RequestMapping("/login")
	public String Login(UserEntity userEntity, HttpServletRequest req, HttpServletResponse response) {
		
		String login = userService.login(userEntity, req, response);
		if (login.equals(Constants.SUCCESSCODE)) {
			// 登录成功
			log.info("UserController/Login, 用户登录成功");
			//判断权限
			String userName = userEntity.getUserName();
			UserEntity user = userMapping.queryUserByUserName(userName);
//			return authController.auth(user, userName, req, response);
			return "wastedWater";
		} else if (login.equals(Constants.ERROR)) {
			// 登录失败
			req.setAttribute(Constants.INFORMATION, Constants.LOGINERROE);
			log.info("UserController/Login, 用户登录失败");
			// 返回登录页面，缓存刚才登录的账户
			req.setAttribute(Constants.USERNAME, userEntity.getUserName());
			return Constants.LOGIN;// 用户名或密码错误
		} else {
			// 请先注册
			log.info("账号不存在，请先注册");
			req.setAttribute(Constants.INFORMATION, Constants.SIGNFIRST);
			return Constants.LOGIN;// 需要注册
		}
	}

	/*
	 * 登录页跳转到注册页
	 */
	@RequestMapping("/regist")
	public String regist(HttpServletRequest req) {
		// 将注册页面的部门、岗位两个选项的下拉框动态给前端
	//		List<DepartmentEntity> allDepart = userService.getAllDepart();
	//		List<PostEntity> allPost = userService.getAllPost();
//		req.setAttribute(Constants.DEPARTMENT, allDepart);
//		req.setAttribute(Constants.POST, allPost);
		return Constants.SIGNUP;
	}
	
	/**
	 * 个人中心
	 * @param postId
	 * @param req
	 * @return
	 */
	@RequestMapping("/personalCenterWeb")
	public String personalCenterWeb(String postId, HttpServletRequest req, HttpServletResponse response) {
		
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
		UserEntity userEntity = userMapping.queryUserByUserName(userName);
		// 在userEntity里面填充postid-->postName, departid --> departname;
//		userEntity.setDepartmentName(departmentMapping.getNameById(userEntity.getDepartmentId()));
//		userEntity.setPostName(postMapping.getNameById(userEntity.getPostId()));


//		头像
//		 ServletOutputStream outputStream = null;
//		    try {
//		    	byte[] bytes = null;
//		    	Path path =Path.of(userEntity.getIcon());
//		    	bytes = Files.readAllBytes(path);
//		    	
//		        outputStream = response.getOutputStream();
//		        outputStream.write(bytes);
//		        outputStream.flush();
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		        if (outputStream != null) {
//		            outputStream.close();
//		        }
//		    }

			
			
		req.setAttribute("INFORMATION", userEntity);
		req.setAttribute(Constants.POSTID, postId);
		return Constants.PERSONALCENTER;
	}
	
/**
 * 	修改头像
 */
	@RequestMapping("/iconUpdate")
	public String iconUpdate(String username, @RequestParam(value = "file", required = false) List<MultipartFile> file,
			HttpServletRequest req, HttpServletResponse resp, String postId) throws IOException {

			userService.iconUpdate(username, file, resp);
			return personalCenterWeb(postId, req, resp);
		
	}
	
	
	/**
	 * 退出登录
	 * @param resp
	 * @return
	 */
	@RequestMapping("/loginout")
	public String loginout(HttpServletResponse resp) {
		// 清除Cookie信息
		Cookie cookie = new Cookie(Constants.COOKIEHEAD, "");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
		return Constants.LOGIN;
	}
	
	/**
	 * 修改密码
	 * @param id
	 * @param newpwd
	 * @return
	 */
	public String fixpwd(int id, String newpwd) {
		// id -> userentity
		// userentity 更新pwd字段
		// update数据库
		return Constants.LOGIN;
	}
	
	
	@RequestMapping("/delete")
	public String delete(HttpServletRequest req) {
		// 将注册页面的部门、岗位两个选项的下拉框动态给前端
		userMapping.deleteUserByUserName("qh-02");
		return Constants.SUCCESS;
	}
	
	//20220830-thg,修改邮箱
	/**
	 * 
	 * @param req
	 * @param response
	 * @param username
	 * @param email
	 * @param code
	 * @param companyEntity
	 * @param postId
	 * @param session
	 * @return
	 */
	@RequestMapping("/updateEmail")
//	public String updateEmail(HttpServletRequest req, HttpServletResponse response, String username, String email,String code, CompanyNameEntity companyEntity, String postId, HttpSession session) {
	public String updateEmail(HttpServletRequest req, HttpServletResponse response, String username, String email,String code, String postId, HttpSession session) {
//		req.setAttribute(Constants.COMPANYNAMEENTITY, companyEntity);
		req.setAttribute(Constants.POSTID, postId);
		String updateEmail = userService.updateEmail(username, email, code, session,req);
		if(updateEmail.equals(Constants.FAILCODE)) {
			log.info("UserController/updateEmail:修改邮箱失败");
			return updateEmailWeb(req, username, email, postId);
		}
		else {
			log.info("UserController/updateEmail:修改邮箱成功");
			req.setAttribute("msg", "修改邮箱成功");
			return personalCenterWeb(postId, req, response);
		}
		
	}
	//20220830-thg,获得邮箱验证码
	/**
	 * 
	 * @param username
	 * @param email
	 * @param session
	 * @param req
	 * @param companyEntity
	 * @param postId
	 * @return
	 */
	@RequestMapping("/getEmailCode")
//	public String getEmailCode(String username,String email, HttpSession session, HttpServletRequest req, CompanyNameEntity companyEntity, String postId) {
	public String getEmailCode(String username,String email, HttpSession session, HttpServletRequest req, String postId) {
				
		String getEmailCode = userService.getUpdateEmailCode(username, email, session);
		if(getEmailCode.equals(Constants.FAILCODE)) {
			log.info("UserController/getEmailCode:发送验证码失败");
		}
		else {
			log.info("UserController/getEmailCode:发送验证码成功");
		}
		return updatePwdWeb1(req);
	}
	
	//20220830-thg,修改邮箱页面
	/**
	 * 
	 * @param req
	 * @param username
	 * @param companyEntity
	 * @param postId
	 * @return
	 */
	@RequestMapping("/updateEmailWeb")
//	public String updateEmailWeb(HttpServletRequest req, String username,String email, CompanyNameEntity companyEntity, String postId) {
	public String updateEmailWeb(HttpServletRequest req, String username,String email, String postId) {
//		req.setAttribute(Constants.COMPANYNAMEENTITY, companyEntity);
		req.setAttribute(Constants.POSTID, postId);
		req.setAttribute("userName", username);
		req.setAttribute("newEmail", email);
		return Constants.UPDATEEMAIL;
	}
	
	//20220915-thg,修改手机号
		/**
		 * 
		 * @param req
		 * @param response
		 * @param username
		 * @param email
		 * @param code
		 * @param companyEntity
		 * @param postId
		 * @param session
		 * @return
		 */
		@RequestMapping("/updatePhone")
//		public String updatePhone(HttpServletRequest req, HttpServletResponse response, String username, String phone,String code, CompanyNameEntity companyEntity, String postId, HttpSession session) {
		public String updatePhone(HttpServletRequest req, HttpServletResponse response, String username, String phone,String code, String postId, HttpSession session) {

//			req.setAttribute(Constants.COMPANYNAMEENTITY, companyEntity);
			req.setAttribute(Constants.POSTID, postId);
			String updateEmail = userService.updatePhone(username, phone, code, session,req);
			if(updateEmail.equals(Constants.FAILCODE)) {
				log.info("UserController/updateEmail:修改手机失败");
//				return updatePhoneWeb(req, username, phone,companyEntity, postId);
				return updatePhoneWeb(req, username, phone, postId);
			}
			else {
				log.info("UserController/updateEmail:修改手机成功");
				req.setAttribute("msg", "修改手机成功");
				return personalCenterWeb(postId, req, response);
			}
			
		}
		
	

	//20220915-thg,修改手机页面
	/**
	 * 
	 * @param req
	 * @param username
	 * @param companyEntity
	 * @param postId
	 * @return
	 */
	@RequestMapping("/updatePhoneWeb")
//	public String updatePhoneWeb(HttpServletRequest req, String username,String phone, CompanyNameEntity companyEntity, String postId) {
	public String updatePhoneWeb(HttpServletRequest req, String username,String phone, String postId) {

//		req.setAttribute(Constants.COMPANYNAMEENTITY, companyEntity);
		req.setAttribute(Constants.POSTID, postId);
		req.setAttribute("userName", username);
		req.setAttribute("newPhone", phone);
		return Constants.UPDATEPHONE;
	}
	
	
	//20220915-thg,修改密码验证页面
	@RequestMapping("updatePwdWeb1")
	public String updatePwdWeb1(HttpServletRequest req) {
		return Constants.UPDATEPWD1;
	}
	
	@RequestMapping("updatePwdWeb2")
	public String updatePwdWeb2(HttpServletRequest req, String username) {
		try {
			if(userMapping.queryUserByUserName(username)==null) {
				req.setAttribute("msg", "查询不到该用户名，请确认用户名是否有误。");
				return Constants.UPDATEPWD1;
			}
			String phone = userMapping.getPhoneByUserName(username);
			String email = userMapping.getEmailByUserName(username);
			req.setAttribute("phone", phone);
			req.setAttribute("email", email);
			req.setAttribute("userName", username);
			log.info("查询手机和邮箱成功");
			return Constants.UPDATEPWD2;
		}catch (Exception e) {
			// TODO: handle exception
			log.info("UserController.updatePwdWeb2:查询手机和邮箱失败",e);
		}
		return Constants.UPDATEPWD1;
	}
	
	//20220915-thg,修改密码验证身份
	@RequestMapping("updatePwdVerify")
	public String updatePwdVerify(String username, String emailCode, String phoneCode, HttpSession session, HttpServletRequest req) {
		
		if(phoneCode!=null) {
			log.info(username+"正在使用手机验证修改密码");
			if(phoneCode.equals(session.getAttribute("phonecode"))) {
				req.setAttribute("userName", username);
				log.info(username+":手机验证码正确");
				return Constants.UPDATEPWD;
			}
			else {
				log.info(username+":手机验证码错误");
				req.setAttribute("msg", "验证码错误或过期！");
				return updatePwdWeb2(req, username);
			}
		}
		else if(emailCode!=null) {
			log.info(username+"正在使用邮箱验证修改密码");
			if(emailCode.equals(session.getAttribute("code"))) {
				req.setAttribute("userName", username);
				log.info(username+":邮箱验证码正确");
				return Constants.UPDATEPWD;
			}
			else {
				log.info(username+":邮箱验证码错误");
				req.setAttribute("msg", "验证码错误或过期！");
				return updatePwdWeb2(req, username);
			}
		}
		else {
			log.info(username+":两个验证码均为空，无法验证");
			req.setAttribute("msg", "您的验证码为空！");
			return updatePwdWeb2(req, username);
		}
	}
	
	//20220916-thg,修改密码
	@RequestMapping("/updatePwd")
	public String updatePwd(String username, String password, HttpServletRequest req) {
		String flag = userService.updatePwd(username, password);
		if(flag.equals(Constants.FAILCODE)) {
			log.info("userController/updatePwd:修改密码失败");
			req.setAttribute("msg", "修改密码失败，请重试");
			return updatePwdWeb1(req);
		}
		else {
			log.info("userController/updatePwd:修改密码成功");
			req.setAttribute("msg", "密码修改成功，请返回登录");
			return Constants.LOGIN;
		}
	}
}
