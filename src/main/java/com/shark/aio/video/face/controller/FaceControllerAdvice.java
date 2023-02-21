package com.shark.aio.video.face.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FaceControllerAdvice {

    @ExceptionHandler({NoSuchFieldException.class, IllegalAccessException.class})
    public ModelAndView addSessionExceptionHandler(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("msg","推流进程操作出现异常！");
        modelAndView.setViewName("videoPlay");
        return modelAndView;
    }
}
