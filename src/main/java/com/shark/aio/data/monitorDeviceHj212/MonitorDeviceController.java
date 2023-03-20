package com.shark.aio.data.monitorDeviceHj212;

import com.shark.aio.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lbx
 * @date 2023/3/19 - 15:08
 **/
@Slf4j
@Controller
public class MonitorDeviceController {
    @Autowired
    MonitorDeviceService monitorDeviceService;
    /**
     * 跳转到新增污染源页面
     * @param request request
     * @return 新增污染源页面
     */
    @GetMapping("/pollution/add")
    public String toAddPollutionPage(HttpServletRequest request){
        if (!monitorDeviceService.searchMonitor(request)){
            return "500";
        }
        return Constants.ADDPOLLUTION;
    }

    /**
     * 新增监测点或设备关联
     * 监测点不能重复
     * @param request
     * @param monitorDeviceEntity
     * @param newMonitorName 新监测点名称
     * @param existMonitorName 已有的监测点名称
     * @return 操作成功返回预警设置页，否则返回新增页
     */
    @PostMapping("/pollution/submit/add")
    public String managMonitorDevice(HttpServletRequest request, MonitorDeviceEntity monitorDeviceEntity,String newMonitorName,
                                     String existMonitorName, String deleteDeviceId, String deleteMonitorName){
        String msg = monitorDeviceService.managMonitorDevice(monitorDeviceEntity,newMonitorName,existMonitorName, deleteDeviceId, deleteMonitorName);
        request.setAttribute(Constants.MSG, msg);
//        if (msg.contains("成功")) return "forward:/pollutionMonitor";
//        else return toAddPollutionPage(request);
        return toAddPollutionPage(request);
    }
}
