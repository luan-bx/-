package com.shark.aio.base.information;

import com.shark.aio.alarm.contactPart.ContractPartMapping;
import com.shark.aio.alarm.contactPart.PartHostEntity;
import com.shark.aio.data.monitorDeviceHj212.MonitorDeviceService;
import com.shark.aio.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author lbx
 * @date 2023/4/12 - 17:15
 **/
@Slf4j
@Controller
@MapperScan(value = "com.shark.aio.base.information")
public class InformationController {
    @Autowired
    MonitorDeviceService MonitorDeviceService;
    @Autowired
    InformationMapping informationMapping;
    @Autowired
    ContractPartMapping contractPartMapping;

    @RequestMapping("/index")
    public String indexWeb(HttpServletRequest request) {
        request.setAttribute("stream",new String[]{"room","room","room","room","room","room","room","room","room"});
        InformationEntity informationEntity = informationMapping.getInformation();
        List<String> allMonitor = MonitorDeviceService.getAllMonitor();
        request.setAttribute(Constants.ALLMONITOR, allMonitor);
        request.setAttribute("informationEntity", informationEntity);
        PartHostEntity partHostEntity = contractPartMapping.getPartHost();
        request.setAttribute("partHostEntity", partHostEntity);
        HttpSession session = request.getSession();
        session.setAttribute("company",informationEntity.getCompany());
        log.info("进入首页成功！");
        return "index";
    }

    @RequestMapping("/updateInformation")
    public String updateInformationWeb(HttpServletRequest request) {
        InformationEntity informationEntity = informationMapping.getInformation();
        List<String> allMonitor = MonitorDeviceService.getAllMonitor();
        PartHostEntity partHostEntity = contractPartMapping.getPartHost();
        request.setAttribute("partHostEntity", partHostEntity);
        request.setAttribute(Constants.ALLMONITOR, allMonitor);
        request.setAttribute("informationEntity", informationEntity);
        log.info("进入企业信息修改页面成功！");
        return "updateInformation";
    }

    @RequestMapping("/updataInformationEntity")
    public String updataInformationEntity(HttpServletRequest request, InformationEntity informationEntity) {
        try{
            informationMapping.updateInformation(informationEntity);
        }catch (Exception e){
            log.error("企业信息修改失败！" +e);
            return updateInformationWeb(request);
        }
        request.setAttribute("msg", "企业信息修改成功！");
        log.info("企业信息修改成功！");
        return indexWeb(request);
    }

    @RequestMapping("/updataPartHostEntity")
    public String updataPartHostEntity(HttpServletRequest request, PartHostEntity partHostEntity) {
        try{
            contractPartMapping.updatePartHost(partHostEntity);
        }catch (Exception e){
            log.error("园区地址修改失败！" +e);
            return updateInformationWeb(request);
        }
        request.setAttribute("msg", "园区地址修改成功！");
        log.info("园区地址修改成功！");
        return indexWeb(request);
    }
}
