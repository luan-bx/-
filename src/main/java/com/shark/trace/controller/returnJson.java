package com.shark.trace.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class returnJson {
    @GetMapping("/book")
    @ResponseBody
    public Book book() {
        Book book = new Book();
        book.setAuthor("罗贯中");
        book.setName("三国演义");
        book.setPrice(10.0);
        return book;
    }

    @GetMapping("/wind-global.json")
    @ResponseBody
    public String windGlobal() {
        String json = ResourceUtil.readUtf8Str("static/json/wind-global.json");
        return json;
    }

    @GetMapping("/wind-gbr.json")
    @ResponseBody
    public String windGbr() {
        String json = ResourceUtil.readUtf8Str("static/json/wind-gbr.json");
        return json;
    }

    @GetMapping("/water-gbr.json")
    @ResponseBody
    public String waterGbr() {
        String json = ResourceUtil.readUtf8Str("static/json/water-gbr.json");
        return json;
    }



}
