package com.shark.aio.data.monitorDeviceHj212;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author DaHuaJia
 * @Description HJ212消息数据处理
 * HJ212数据示例：##0746ST=31;CN=2061;PW=123456;MN=7568770259402;Flag=0;CP=&&DataTime=20221008100000;B02-Min=1.6960,B02-Avg=3.0586,B02-Max=3.7704,B02-Cou=11010.8437;S01-Min=17.7469,S01-Avg=19.4636,S01-Max=19.6944;S02-Min=3.2459,S02-Avg=5.6705,S02-Max=6.9578;S03-Min=30.0434,S03-Avg=30.2675,S03-Max=30.4503;S08-Min=-0.4643,S08-Avg=-0.3541,S08-Max=0.0000;S05-Min=6.1814,S05-Avg=6.8655,S05-Max=7.0097;a24088-Min=2.4957,a24088-Avg=3.2176,a24088-Max=4.7744,a24088-Cou=0.0354;25-Min=6.4292,25-Avg=12.1457,25-Max=20.0606,25-Cou=0.1336;a05002-Min=2.7715,a05002-Avg=7.8561,a05002-Max=14.7934,a05002-Cou=0.0863;17-Min=0.0000,17-Avg=0.0000,17-Max=0.0000,17-Cou=0.0000;18-Min=0.0000,18-Avg=0.0000,18-Max=0.0000,18-Cou=0.0000;16-Min=0.0000,16-Avg=0.0000,16-Max=0.0000,16-Cou=0.0000&&7BC0
 *               ##0169ST=32;CN=3020;PW=123456;MN=010000A8900C0;Flag=5;CP=&&DataTime=20100301145000;PolId=w01018;i12001-Info=%d;i12002-Info=%d;i12003-Info=%d;i22001-Info=%d;i32001-Info=%d&&cd41\r\n
 * @Date 2022-10-09 11:09:16
 */
@Slf4j
public class HJ212MsgUtils {

    /**
     * 传入HJ212数据，返回简单json数据，没有对污染源进行分类汇总。
     *
     * @param msg hj212
     * @return json
     */
    public static JSONObject dealMsg1(String msg) {
        if (msg !=null && !msg.contains("SIZE") && !msg.contains("++")  && !msg.contains("AT")) {
            JSONObject data = new JSONObject();
            try {
                // 拆分消息
                //##0746ST=31;CN=2061;PW=123456;MN=7568770259402;Flag=0;CP=
                //&&DataTime=20221008100000;B02-Min=1.6960,B02-Avg=3.0586,B02-Max=3.7704,B02-Cou=11010.8437;S01-Min=17.7469,S01-Avg=19.4636,S01-Max=19.6944;S02-Min=3.2459,S02-Avg=5.6705,S02-Max=6.9578;S03-Min=30.0434,S03-Avg=30.2675,S03-Max=30.4503;S08-Min=-0.4643,S08-Avg=-0.3541,S08-Max=0.0000;S05-Min=6.1814,S05-Avg=6.8655,S05-Max=7.0097;a24088-Min=2.4957,a24088-Avg=3.2176,a24088-Max=4.7744,a24088-Cou=0.0354;25-Min=6.4292,25-Avg=12.1457,25-Max=20.0606,25-Cou=0.1336;a05002-Min=2.7715,a05002-Avg=7.8561,a05002-Max=14.7934,a05002-Cou=0.0863;17-Min=0.0000,17-Avg=0.0000,17-Max=0.0000,17-Cou=0.0000;18-Min=0.0000,18-Avg=0.0000,18-Max=0.0000,18-Cou=0.0000;16-Min=0.0000,16-Avg=0.0000,16-Max=0.0000,16-Cou=0.0000
                //&&7BC0"));
                String[] subMsg = msg.split("&&");

                // 清洗消息头基本数据
                String headStr = subMsg[0].substring(2).replace(";CP=", "").replace("=", "\":\"")
                        .replace(",", "\",\"").replace(";", "\",\"");
                data.put("SIZE", headStr.substring(0, 4));
                data.putAll(JSONObject.parseObject("{\"" + headStr.substring(4) + "\"}"));

                boolean flag = false;
                String a  = null;
                if(!subMsg[1].contains("DataTime")){
                    flag = true;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    a  = timeFormat.format(new Date());
                }
                // 清洗消息头基本数据
                //分号或者逗号一分
                String[] monitors = subMsg[1].split(";|,");
                //monitors例子
                //DataTime":"20221008100000
                //B02-Min":"1.6960
                JSONObject cp = new JSONObject();
                for (String obj : monitors) {
                    String paramStr = obj.replace("=", "\":\"").replace(",", "\",\"")
                            .replace(";", "\",\"");
                    // 如果是时间信息，则直接放到外层
                    if (paramStr.contains("DataTime") || flag) {
                        // 数据包里有DataTime,进行转换，没有则走的上面定义当前时间
                        if(!flag){
                            //QN转换成日期格式
                            String dataTime = paramStr.substring(paramStr.indexOf(":") + 2);
                            String dataTime2 = dataTime.substring(0, 14);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = formatter.parse(dataTime2);
                            a  = timeFormat.format(date);
                            timeFormat.format(new Date());
                        }

                        data.put("DataTime", a);
                    } else if (paramStr.contains("PoIId")) {
                        data.putAll(JSONObject.parseObject("{\"" + paramStr + "\"}"));
                    } else {
                        String[] chineseField = setChineseField(paramStr);
                        cp.put(chineseField[0], chineseField[1]);
                    }
                }
                data.put("CP", cp);

                // 保存消息尾数据，主要是CRC校验和包结束符
                data.put("End", subMsg[2].substring(0, 4));

            } catch (Exception e) {
                log.error("HJ212数据转JSON错误。报错信息：{}，消息内容：{}", e.getMessage(), msg);
                e.printStackTrace();
            }
            return data;
        }
        return null;
    }

    /**
     * 将数据中的
     */
    private static String[] setChineseField(String data) {
        String key = data.substring(0, data.indexOf("\""));
        data = data.replaceAll(key + "\":\"", "").replaceAll("\":\"", "");
        int OC = Integer.parseInt(data);
        switch (key) {
            case "i12001-Info":
                switch (OC) {
                    case 0:
                        data = "运行";
                        break;
                    case 1:
                        data = "维护";
                        break;
                    case 2:
                        data = "故障";
                        break;
                    case 3:
                        data = "校准";
                        break;
                    case 4:
                        data = "运行";
                        break;
                    case 5:
                        data = "反吹";
                        break;
                    case 6:
                        data = "电源故障";
                        break;
                    case 7:
                        data = "测量";
                        break;
                    case 8:
                        data = "标定";
                        break;
                    case 9:
                        data = "待机";
                        break;
                    case 10:
                        data = "运维";
                        break;
                }
                break;
            case "i12002-Info":
                switch (OC) {
                    case 0:
                        data = "0";
                        break;
                    case 1:
                        data = "0";
                        break;
                }
                break;
            case "i12003-Info":
                switch (OC) {
                    case 0:
                        data = "正常";
                        break;
                    case 1:
                        data = "报警";
                        break;
                }
                break;
            case "i22001-Info":
                switch (OC) {
                    case 0:
                        data = "运行";
                        break;
                    case 1:
                        data = "停机";
                        break;
                    case 2:
                        data = "故障";
                        break;
                    case 3:
                        data = "维护";
                        break;
                }
                break;
            case "i32001-Info":
                switch (OC) {
                    case 0:
                        data = "运行";
                        break;
                    case 1:
                        data = "停机";
                        break;
                    case 2:
                        data = "故障";
                        break;
                    case 3:
                        data = "维护";
                        break;
                }
                break;
        }
        String[] result = new String[2];
        result[0] = key;
        result[1] = data;
        return result;
    }
    /**
     * 传入HJ212数据，返回复杂json数据，对污染源进行分类汇总，不同污染源用不同的json key。
     *
     * @param msg hj212
     * @return json
     */
    public static JSONObject dealMsg2(String msg) {
        JSONObject data = new JSONObject();
        try {
            // 拆分消息
            String[] subMsg = msg.split("&&");

            // 清洗消息头基本数据
            String headStr = subMsg[0].substring(2).replace(";CP=", "").replace("=", "\":\"")
                    .replace(",", "\",\"").replace(";", "\",\"");
            data.put("SIZE", headStr.substring(0, 4));
            data.putAll(JSONObject.parseObject("{\"" + headStr.substring(4) + "\"}"));

            // 清洗数据体基本数据
            String[] monitors = subMsg[1].split(";");
            JSONObject cp = new JSONObject();
            for (String obj : monitors) {
                String paramStr = obj.replace("=", "\":\"").replace(",", "\",\"")
                        .replace(";", "\",\"");
                // 如果是时间信息，则直接放到外层
                if (paramStr.contains("DataTime")) {
                    data.putAll(JSONObject.parseObject("{\"" + paramStr + "\"}"));
                }else if(paramStr.contains("PoIId")){
                    data.putAll(JSONObject.parseObject("{\"" + paramStr + "\"}"));
                }else {
                    String[] ele = getPollutionSource(paramStr);
                    cp.put(ele[0], JSONObject.parseObject("{\"" + ele[1] + "\"}"));
                }
            }
            data.put("CP", cp);

            // 保存消息尾数据，主要是CRC校验和包结束符
            data.put("End", subMsg[2]);

        } catch (Exception e) {
            log.error("HJ212数据转JSON错误。报错信息：{}，消息内容：{}", e.getMessage(), msg);
            e.printStackTrace();
        }
        return data;
    }


    /**
     * 解析污染源数据，获取污染源编号
     */
    private static String[] getPollutionSource(String data) {
        String key = data.substring(0, data.indexOf("-"));
        System.out.println("key" + key);
        data = data.replaceAll(key + "-", "");

        String[] result = new String[2];
        result[0] = key;
        result[1] = data;
        return result;
    }

    /**
     * 校验CRC
     * @param data212
     * @return
     */
    public static String checkData(String data212) {
        try{
            String input = data212.substring(data212.indexOf("QN"), data212.indexOf("\r") -4);

            String length = Integer.toString(input.length());
            String lengthCheck = null;
            if(length.length() == 3){
                lengthCheck = "0" + length;
            }else if (length.length()==2){
                lengthCheck = "00"+length;
            }else if (length.length()==1){
                lengthCheck = "000"+length;
            }
            if(!data212.substring(2, 6).equals(lengthCheck)){
                System.out.println("CRC校验失败");
                return "error";
            };

            int CRC = 0xFFFF;
            int num = 0xA001;
            int inum = 0;
            byte[] sb = input.getBytes();
            for(int j = 0; j < sb.length; j ++) {
                inum = sb[j];
                CRC = (CRC >> 8) & 0x00FF;
                CRC ^= inum;
                for(int k = 0; k < 8; k++) {
                    int flag = CRC % 2;
                    CRC = CRC >> 1;

                    if(flag == 1) {
                        CRC = CRC ^ num;
                    }
                }
            }
            String CRCString= Integer.toHexString(CRC);
            String CRCCheck = null;
            String CRCData = data212.substring(data212.indexOf("\r") - 4, data212.indexOf("\r"));
            if (CRCString.length()==3){
                CRCCheck = "0"+CRCString;
            }else if (CRCString.length()==2){
                CRCCheck = "00"+CRCString;
            }else if (CRCString.length()==1){
                CRCCheck = "000"+CRCString;
            }else {
                CRCCheck = CRCString;
            }

            if (!CRCCheck.equals(CRCData)){
                return "error";
            }
            return CRCCheck;
        }catch(Exception e) {
            log.error("CRC校验失败！",e);
            return "error";
        }

    }

    public static void main(String[] args) {
//        System.out.println(dealMsg1("##0101QN=20160801085857223;ST=32;CN=1062;PW=100000;MN=010000A8900016F000169DC0;Flag=5\n" +
//                ";CP=&&RtdInterval=30&&1C80\r\n"));
//        System.out.println(dealMsg2("##0746ST=31;CN=2061;PW=123456;MN=7568770259402;Flag=0;CP=&&DataTime=20221008100000;B02-Min=1.6960,B02-Avg=3.0586,B02-Max=3.7704,B02-Cou=11010.8437;S01-Min=17.7469,S01-Avg=19.4636,S01-Max=19.6944;S02-Min=3.2459,S02-Avg=5.6705,S02-Max=6.9578;S03-Min=30.0434,S03-Avg=30.2675,S03-Max=30.4503;S08-Min=-0.4643,S08-Avg=-0.3541,S08-Max=0.0000;S05-Min=6.1814,S05-Avg=6.8655,S05-Max=7.0097;a24088-Min=2.4957,a24088-Avg=3.2176,a24088-Max=4.7744,a24088-Cou=0.0354;25-Min=6.4292,25-Avg=12.1457,25-Max=20.0606,25-Cou=0.1336;a05002-Min=2.7715,a05002-Avg=7.8561,a05002-Max=14.7934,a05002-Cou=0.0863;17-Min=0.0000,17-Avg=0.0000,17-Max=0.0000,17-Cou=0.0000;18-Min=0.0000,18-Avg=0.0000,18-Max=0.0000,18-Cou=0.0000;16-Min=0.0000,16-Avg=0.0000,16-Max=0.0000,16-Cou=0.0000&&7BC0"));
//
//        System.out.println("=============" + (new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date()) + "=============");
//        System.out.println(checkData("##0746ST=31;CN=2061;PW=123456;MN=7568770259402;Flag=0;CP=&&DataTime=20221008100000;B02-Min=1.6960,B02-Avg=3.0586,B02-Max=3.7704,B02-Cou=11010.8437;S01-Min=17.7469,S01-Avg=19.4636,S01-Max=19.6944;S02-Min=3.2459,S02-Avg=5.6705,S02-Max=6.9578;S03-Min=30.0434,S03-Avg=30.2675,S03-Max=30.4503;S08-Min=-0.4643,S08-Avg=-0.3541,S08-Max=0.0000;S05-Min=6.1814,S05-Avg=6.8655,S05-Max=7.0097;a24088-Min=2.4957,a24088-Avg=3.2176,a24088-Max=4.7744,a24088-Cou=0.0354;25-Min=6.4292,25-Avg=12.1457,25-Max=20.0606,25-Cou=0.1336;a05002-Min=2.7715,a05002-Avg=7.8561,a05002-Max=14.7934,a05002-Cou=0.0863;17-Min=0.0000,17-Avg=0.0000,17-Max=0.0000,17-Cou=0.0000;18-Min=0.0000,18-Avg=0.0000,18-Max=0.0000,18-Cou=0.0000;16-Min=0.0000,16-Avg=0.0000,16-Max=0.0000,16-Cou=0.0000&&7BC0"));
//
//        System.out.println("=============" + (new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date()) + "=============");

    }

}
