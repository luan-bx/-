package com.shark.user.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shark.user.entity.UserEntity;
import com.shark.util.Constants;
import com.shark.util.MD5Util;
import com.shark.util.VerCodeGenerateUtil;
import com.shark.user.mapper.UserMapping;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class UserService {
	@Autowired
	private UserMapping UserMapping;
//	@Autowired
//	private RedisService redisService;
	@Autowired    
	private JavaMailSenderImpl javaMailSender;  

	/*
	 * 注册
	 */

	/*
	 * 功能：添加用户，插入用户记录 返回： 1. 错误信息 2. 数据库id
	 */

	public String addUser(UserEntity userEntity, List<MultipartFile> file, HttpServletRequest req) {
		if (userEntity == null) {
			return Constants.FAILCODE;
//			没有注册信息传来，弹窗“注册失败，请重新注册”，返回注册第一页
		}
		try {
			// 创建文件夹(头像)
			String docPath = Constants.FILEPATH + Constants.USERS + userEntity.getUserName() + "/";
			File localPath = new File(docPath);
			if (!localPath.exists()) { // 获得文件目录，判断目录是否存在，不存在就新建一个
				localPath.mkdirs();
			}
			// 存文件（头像）
			for(MultipartFile f : file) {
				String filename = f.getOriginalFilename(); // 文件名
				String path = docPath + filename;
				File filePath = new File(path);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));// 保存文件到目录下
				out.write(f.getBytes());// 在创建好的文件中写入f.getBytes()
				out.flush();
				out.close();
//				userEntity.setIcon(path);
				userEntity.setIcon(userEntity.getUserName() + "/" + filename);
			}
			// 用户名+密码 进行MD5加密，再储存数据库
			String name = userEntity.getUserName();
			String password = userEntity.getPassword();
			String MD5password = MD5Util.MD5(name + password);
			userEntity.setPassword(MD5password);
			UserMapping.insert(userEntity);
			return Constants.SUCCESSCODE;
		} catch (Exception e) {
			log.info("插入user记录失败", e);
			req.setAttribute("error", "注册失败，请重新注册或联系管理员");
			return Constants.FAILCODE;
		}
	}

//	/**
//	 * 返回所有岗位信息
//	 * 
//	 * @return
//	 */
//	public List<PostEntity> getAllPost() {
//		return PostMapping.getAll();
//	}


	/*
	 * 功能：微信扫码注册过，从数据库取wxId绑定用户名-密码表 返回： 1. 绑定成功 Constants.SUCCESSCOD 2. 绑定失败
	 * Constants.ERROR
	 */
	public String bindUser(String phone) {
		if (phone == null) {
			return Constants.FAILCODE; // 如果phone为空，直接退出判断
		}
		try {
			// 通过手机号从wx表获取wxid
			int wxId = UserMapping.getWxIdByPhone(phone);
			// wxid更新到user表的wxid字段(前提：user记录已经存在)
			UserMapping.updateWxIdByPhone(wxId, phone);
			return Constants.SUCCESSCODE;
		} catch (Exception e) {
			return Constants.FAILCODE;
		}
	}

	/*
	 * 功能：根据userName、phone、email判断是否是新用户 返回： 1. 是新用户 Constants.SUCCESSCOD 2. 已存在账号
	 * 3. 查询错误 Constants.ERROR
	 */
	public String isNewUser(UserEntity userEntity, HttpServletRequest req) {
		
		try {
//			如果空数据发过来如何判断
			UserEntity NAME = UserMapping.queryUserByUserName(userEntity.getUserName());
			UserEntity PHONE = UserMapping.queryUserByPhone(userEntity.getPhone());
			UserEntity EMAIL = UserMapping.queryCuByEmail(userEntity.getEmail());
			UserEntity number = UserMapping.queryUserByNumber(userEntity.getNumber());
			if (NAME != null) {
				log.info("该用户名已注册过");
				req.setAttribute(Constants.ALREADY, "该用户名已经注册过，请返回登录");
				return Constants.FAILCODE;
			} else if (PHONE != null) {
				log.info("该手机号已注册过");
				req.setAttribute(Constants.ALREADY, "该手机号已经注册过，请返回登录");
				return Constants.FAILCODE;
			} else if (EMAIL != null) {
				log.info("该邮箱已注册过");
				req.setAttribute(Constants.ALREADY, "该邮箱已经注册过，请返回登录");
				return Constants.FAILCODE;
			} else if (number != null) {
				log.info("该工号已注册过");
				req.setAttribute(Constants.ALREADY, "该工号已经注册过，请返回登录");
				return Constants.FAILCODE;
			} else {
				return Constants.SUCCESSCODE;
			}
		} catch (Exception e) {
			log.info("UserService/isNewUser 查询用户错误", e);
			req.setAttribute("error", "注册失败，请重新注册或联系管理员");
			return Constants.ERROR;
		}
	}

	/*
	 * 登录
	 */
	public String login(UserEntity userEntity, HttpServletRequest req, HttpServletResponse response) {
		// 用户名、手机号、邮箱都在loginStr接收
		String loginStr = userEntity.getUserName();
		String password = userEntity.getPassword();
		// 判断何种方式登录
		UserEntity user = null;
		try {
			user = UserMapping.queryUserByUserName(loginStr);
			// 用户名？
			if (user == null) {
				user = UserMapping.queryUserByPhone(loginStr);
				// 手机号？
				if (user == null) {
					user = UserMapping.queryCuByEmail(loginStr);
					// 邮箱？
					if (user == null) {
						// 未注册！
						log.info("UserService/login, 账号不存在，请先注册，本次登录账户：" + loginStr);
						req.setAttribute("error", "账号不存在，请先注册");
						return Constants.FAILCODE;
					} else {
						log.info("UserService/login, 使用邮箱登录 : " + loginStr);
					}
				} else {
					log.info("UserService/login, 使用手机号登录 : " + loginStr);
				}
			} else {
				log.info("UserService/login, 使用用户名登录 : " + loginStr);
			}
			// 查询到用户，进行登录密码校验
			// 此user为从数据库取到的用户全部信息，不要用userEntity
			String name = user.getUserName();
			String loginPs = MD5Util.MD5(name + password);
			log.info("MD5加密" + loginPs);
			if (loginPs.equals(user.getPassword())) {
				// 加缓存，24小时内再次登录无需验证
				Cookie cookie = new Cookie(Constants.COOKIEHEAD, name);
				cookie.setMaxAge(Constants.COOKIE_TTL); // 24h
				response.addCookie(cookie);
				log.info("UserService/login, 登录成功");
//				// 登录成功，写Redis
//				try {
//					setRedis(user);
//				} catch (Exception e) {
//					// TODO: handle exception
//					log.info("UserService/login, redis设置登录信息失败，", e);
//				}
				return Constants.SUCCESSCODE;
			} else {
				log.info("UserService/login, 登录失败，用户名或密码错误，账户：" + loginStr + " 密码：" + password);
				return Constants.ERROR;
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.info("UserService/login, 账号查询失败，请先注册或重新登录", e);
			return Constants.ERROR;
		}
	}

	/*
	 * Redis设置 Redis结构： { userOpenId: {username, phone, password}; 第一此登录存储， expire
	 * 3天 username：userOpenId; 第一此登录存储， expire 3天 phone: userOpenId; 第一此登录存储， expire
	 * 3天 } 用户名-密码的缓存
	 */
//	public void setRedis(UserEntity user) {
//		redisService.redisSetList(Constants.OPENID, user.getUserName());
//		redisService.redisSetList(Constants.OPENID, user.getPhone());
//		redisService.redisSetList(Constants.OPENID, user.getPassword());
//		redisService.redisSetString(user.getUserName(), Constants.OPENID);
//		redisService.redisSetString(user.getPhone(), Constants.OPENID);
//		// 过期时间设置
//		redisService.expire(Constants.OPENID, Constants.LoginTime);
//		redisService.expire(user.getUserName(), Constants.LoginTime);
//		redisService.expire(user.getPhone(), Constants.LoginTime);
//	}

//	/*
//	 * Post
//	 */
//	public List<PostEntity> getPost() {
//		try {
//
//			List<PostEntity> all = PostMapping.getAll();
//			log.info("UserService/getPost, 获取Post成功");
//			return all;
//		} catch (Exception e) {
//			// TODO: handle exception
//			log.info("UserService/getPost, 获取Post失败, ", e);
//			return new ArrayList<PostEntity>();
//		}
//	}

//	/*
//	 * 转数据格式
//	 */
//	public int postId(String name) {
//		return PostMapping.getIdByName(name);
//	}
//
//	public int departmentId(String name) {
//		return DepartmentMapping.getIdByName(name);
//	}

	/*
	 * 更新头像
	 */
	public void iconUpdate(String username, @RequestParam(value = "file", required = false) List<MultipartFile> file,
			HttpServletResponse resp) throws IOException {
		// 删除原头像
		String docPath = Constants.FILEPATH + Constants.USERS + username + "/";
		delAllFile(docPath);
		// 存文件（头像）
		for (MultipartFile f : file) {
			String filename = f.getOriginalFilename(); // 文件名
			String path = docPath + filename;
			File filePath = new File(path);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));// 保存文件到目录下
			out.write(f.getBytes());// 在创建好的文件中写入f.getBytes()
			out.flush();
			out.close();
			UserMapping.updateIconByUserName(username, username + "/" + filename);
			Cookie cookie = new Cookie("iconPath", username + "/" + filename);
			resp.addCookie(cookie);
		}
	}
	
	/*
	 * 删除目录内所有文件
	 */
	public static boolean delAllFile(String path) {
	    boolean flag = false;
	    File file = new File(path);
	    if (!file.exists()) {  return flag; }
	    if (!file.isDirectory()) { return flag;}
	    String[] tempList = file.list();
	    File temp = null;
	    for (int i = 0; i < tempList.length; i++) {
	        if (path.endsWith(File.separator)) {
	            temp = new File(path + tempList[i]);
	        } else {
	            temp = new File(path + File.separator + tempList[i]);
	        }
	        if (temp.isFile()) {
	            temp.delete();
	        }
	        if (temp.isDirectory()) {
	            delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	            flag = true;
	        }
	    }
	    return flag;
	 
	}
	
	//20220830-thg,修改邮箱获得验证码
	public String getUpdateEmailCode(String username, String email, HttpSession session) {
		String code = VerCodeGenerateUtil.generateVerCode();
		SimpleMailMessage mailMessage = new SimpleMailMessage();        
		mailMessage.setSubject("项目管理系统-邮箱验证码");//主题        
		mailMessage.setText("尊敬的"+username+":\n\t您的邮箱验证码为："+code+",有效期为5分钟。");//内容        mailMessage.setTo("xxxx@163.com");        
		mailMessage.setFrom("982857267@qq.com");  
		mailMessage.setTo(email);  
		try {
			javaMailSender.send(mailMessage);
			session.setMaxInactiveInterval(300);
			session.setAttribute("email", email);
			session.setAttribute("code", code);
			return Constants.SUCCESSCODE;
		}catch(Exception e) {
			log.info("UserService/updateEmail:",e);
			return Constants.FAILCODE;
		}
	}
	//20220830-thg,修改邮箱
	public String updateEmail(String username, String email,String code, HttpSession session, HttpServletRequest req) {
		if(!email.equals(session.getAttribute("email"))) {
			req.setAttribute("msg","邮箱不一致或验证码过期！");
			return Constants.FAILCODE;
		}
		if(!code.equals(session.getAttribute("code"))) {
			req.setAttribute("msg","验证码错误！");
			return Constants.FAILCODE;
		}
		try {
			UserMapping.updateEmailByUserName(email, username);
			return Constants.SUCCESSCODE;
		} catch (Exception e) {
			// TODO: handle exception
			log.info("UserService/updateEmail:更改邮箱失败");
			req.setAttribute("msg", "插入数据库失败");
			return Constants.FAILCODE;
		}
	}
	
	//20220915-thg,修改手机号
	public String updatePhone(String username, String phone, String code, HttpSession session, HttpServletRequest req) {
		if(!phone.equals(session.getAttribute("phone"))) {
			req.setAttribute("msg", "手机号前后不一致！");
			return Constants.FAILCODE;
		}
		if(!code.equals(session.getAttribute("phonecode"))) {
			req.setAttribute("msg", "验证码错误或过期！");
			return Constants.FAILCODE;
		}
		try {
			UserMapping.updatePhoneByUserName(phone, username);
			return Constants.SUCCESSCODE;
		}catch (Exception e) {
			// TODO: handle exception
			log.info("UserService/updatePhone:更改手机失败");
			req.setAttribute("msg", "插入数据库失败");
			return Constants.FAILCODE;
		}
	}
	
	//20220916-thg,修改密码
	public String updatePwd(String username, String password) {
		String MD5Pwd = MD5Util.MD5(username+password);
		try {
			UserMapping.updatePwdByUserName(MD5Pwd, username);
			return Constants.SUCCESSCODE;
		}catch (Exception e) {
			// TODO: handle exception
			log.info("UserService/updatePwd:修改密码失败",e);
			return Constants.FAILCODE;
		}
	}
}
