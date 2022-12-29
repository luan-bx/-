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
	String FILEPATH = "/Users/lb/Downloads/shark/环保仪/data/";
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
	String INFORMATION ="information";
	String INFORMATIONID ="informationId";
	String DEPARTMENT ="department";
	String POST ="post";
	String LOGINERROE ="loginerror";
	String SIGNFIRST ="signfirst";
	String USERNAME = "userName";
	String DESCRIPTION = "description";
	String POSTID = "postId";
	String PRONAME = "proname";
	String PENDING = "pending";
	String PROCESSING = "processing";
	String PAYMENTTREM = "paymentTerm";
	String ELEENGINEER = "eleEngineer";
	String SOFENGINEER = "sofEngineer";
	String MECENGINEER = "MecEngineer";
	String ELEENGINEERNoBUSY = "eleEngineerNoBusy";
	String SOFENGINEERNoBUSY = "sofEngineerNoBusy";
	String MECENGINEERNoBUSY = "mecEngineerNoBusy";
	/*
	 * 页面
	 */
	String LOGIN = "login";
	String INDEX = "index";
	String SIGNUP = "signup";
	String NEW = "new";
	String REVIEW = "review";
	String REVIEWCHECK ="review-check";
	String SIGN = "sign";
	String SIGNCHECK = "sign-check";
	String DESIGNCHECK = "design-check";
	String DESIGN = "design";
	String REDESIGN = "reDesign";
	String ELEDESIGN = "ele-design";
	String MECDESIGN = "mec-design";
	String SOFDESIGN = "sof-design";
	String PRODUCTOUT = "product-out";
	String PRODUCTPROCESS = "product-process";
	String PRODUCTPURCHASE = "product-purchase";
	String PRODUCTCHECK = "product-check";
	String ASSEMBLE = "assemble";
	String ASSEMBLECHECK = "assemble-check";
	String SUCCESS = "success";
	String ALLDETAIL ="allDetail";
	String PERSONALCENTER="personal-center";
	String REASSEMBLE ="reAssemble";
	String WAIT = "wait";
	
	String REVIEWDETAIL ="reviewDetail";
	String SIGNDETAIL ="signDetail";
	String DESIGNDETAIL ="designDetail";
	String DESIGNALLDETAIL ="designAllDetail";
	String MECDESIGNDETAIL ="mecDesignDetail";
	String ELEDESIGNDETAIL ="eleDesignDetail";
	String SOFDESIGNDETAIL ="sofDesignDetail";
	String PRODUCTOUTDETAIL ="productOutDetail";
	String PRODUCTPROCESSDETAIL ="productProcessDetail";
	String PRODUCTPURCHASEDETAIL ="productPurchaseDetail";
	String PRODUCTALLDETAIL ="productAllDetail";
	String ASSEMBLEDETAIL ="assembleDetail";
	
	String UPDATEEMAIL = "updateEmail";
	String UPDATEPHONE = "updatePhone";
	String UPDATEPWD1 = "updatePwd1";//修改密码第一步：输入用户名
	String UPDATEPWD2 = "updatePwd2";//修改密码第二部：输入手机邮箱验证码
	String UPDATEPWD = "updatePwd";//修改密码最后一步：输入新密码

	
	/*
	 * 侧边栏-返回项目信息列表
	 */
	
	String REVIEWING = "reviewing";
	String SIGNING = "signing";
	String DESIGNING = "designing";
	String ASSEMBLING = "assembling";
	/*
	 * 信息默认值
	 */
	
	String NEWENTITY = "newEntity";
	String SIGNENTITY = "signEntity";
	String DESIGNENTITY = "designEntity";
	String PRODUCTOUTSOURCEENTITY = "productOutsourceEntity";
	String PRODUCTPROCESSENTITY = "productProcessEntity";
	String PRODUCTPURCHASEENTITY = "productPurchaseEntity";
	String ASSEMBLEENTITY = "assembleEntity";
	String USERENTITY = "userEntity";
	String DESIGNMECHANCISENTITY = "designMechancisEntity";
	String DESIGNSOFTWAREENTITY = "designSoftwareEntity";
	String DESIGNELECTRICALENTITY = "designElectricalEntity";
	
	/*
	 * 后台管理系统
	 */
	String BACKENDSYSTEM = "backEndSystem/";
	String ALLUSER = "allUser";
	String ALLPAYMENTTERM = "allPaymentTerm";
	String COMPANYNAMEENTITY = "companyNameEntity";
	String CONSTRUCTION = "construction";
	String PAYMENT = "payment";
	String PROJECTMANAGE = "projectManage";
	String PROJECTDETAIL = "projectDetail";
	String SIGNUPDATE = "signUpdate";
	String DESIGNUPDATE = "designUpdate";
	String MECELESOFDETAIL = "mecelesofDetail";
	String OUTPURPRODETAIL = "outPurProDetail";
	String PROCESSMANAGE = "processManage";
	
}
