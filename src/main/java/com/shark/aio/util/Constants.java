package com.shark.aio.util;

public interface Constants {

	/*
	 * 返回值
	 */
	String SUCCESSCODE = "200";
	String FAILCODE = "500";
	String ERROR = "error";
	
	/*
	 * 用户注册
	 */
	String ALREADY = "already";
	String BINDSUCCESS = "bindsuccess";
	//String FILEPATH = "/Users/lb/Downloads/shark/环保仪/data/";
	String FILEPATH = "C:/shark/环保仪/data/";
	String USERS = "users/";
	String PROJECT = "project/";
	
	/*
	 * 项目
	 */
	String REQUEST = "/request/";
	String SOW = "sow/";
	String QUOTATION = "quotation/";

	
	String[] statuss = new String[] {"项目新建", "项目评审中", "项目签订中", "项目设计中", "项目生产中", "项目装配中", "项目审核不通过", "项目已完成"};
	
	/*
	 * Redis
	 */
	long LoginTime = 3 * 24 * 60 * 60L; // Redis缓存3天
	String OPENID = "OPENID";
	
	/*
	 * cookie
	 */
	String COOKIEHEAD = "cookiehead";
	int COOKIE_TTL = 60 * 60 * 24; //24h
	
	/*
	 * 返回前端
	 */
	String MSG = "msg";
	String ALLMONITORCLASS = "allMonitorClass";
	String ALLMONITOR = "allMonitor";
	String ALLPOLLUTIONNAME = "allPollutionName";
	String INFORMATION ="information";
	String LOGINERROE ="loginerror";
	String SIGNFIRST ="signfirst";
	String USERNAME = "userName";
	String POSTID = "postId";

	/*
	 * 页面
	 */
	String LOGIN = "login";
	String INDEX = "index";
	String SIGNUP = "signup";
	String MAP = "map";
	String TRACE = "trace";
	String ADDPOLLUTION = "addPollution";
	String ALARMSETTINGS = "alarmSettings";
	String ADDALARMSETTING = "addAlarmSetting";
	String EDITALARMSETTING = "editAlarmSetting";
	String ALARMREOCRDS = "alarmRecords";

	String SUCCESS = "success";
	String ALLDETAIL ="allDetail";
	String PERSONALCENTER="personal-center";
	String REASSEMBLE ="reAssemble";
	String WAIT = "wait";

	String UPDATEEMAIL = "updateEmail";
	String UPDATEPHONE = "updatePhone";
	String UPDATEPWD1 = "updatePwd1";//修改密码第一步：输入用户名
	String UPDATEPWD2 = "updatePwd2";//修改密码第二部：输入手机邮箱验证码
	String UPDATEPWD = "updatePwd";//修改密码最后一步：输入新密码


}
