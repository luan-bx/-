package com.shark.aio.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class MyWebMvcConfigurer extends WebMvcConfigurationSupport{
	/**
	* 配置静态访问资源
	* @param registry
	*/
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/background/**").addResourceLocations("classpath:/static/background/");
		registry.addResourceHandler("/build/**").addResourceLocations("classpath:/static/build/");
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/json/**").addResourceLocations("classpath:/static/json/");
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
		registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
		registry.addResourceHandler("/template/**").addResourceLocations("classpath:/template/");
		registry.addResourceHandler("/vendors/**").addResourceLocations("classpath:/vendors/");
		registry.addResourceHandler("/video/**").addResourceLocations("classpath:/video/");
        registry.addResourceHandler("/iconPath/**").addResourceLocations("file:"+ Constants.FILEPATH + "users/");
    }

	 
}
