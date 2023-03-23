package com.cntaiping.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.JSON;
import com.cntaiping.domain.HttpJsonParam;
import com.cntaiping.domain.QaReq;
import com.cntaiping.domain.QaResp;
import com.cntaiping.service.ExcelService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelServiceImpl implements ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private static final String FILE_NAME = "F:\\QA.xlsx";
    private static final String URL = "https://bottest.life.cntaiping.com/bot_dazhi/api/ali/chat/qa";
    /**
     * 渠道id
     */
    private static final String CHANNEL = "lbt";
    private static final String USER_ID = "玛丽萝丝";
//    private static final String AGENT_ORGAN_ID = "1";
    /**
     * 机构id
     */
    private static final String ORGAN_CODE = "12640";


    private static final String CATEGORY = "2";
    private static final String AGENT_CHANNEL = "2";



    @Override
    public void HandleExcelQuestion(List<QaReq> cachedDataList) {
        List<QaResp> respList = new ArrayList<>();
        cachedDataList.forEach(qaReq ->{
            HttpJsonParam hjp  = new HttpJsonParam();
            hjp.setChannel(CHANNEL);
            hjp.setUserId(USER_ID);
//            hjp.setAgentOrganId(AGENT_ORGAN_ID);
            hjp.setCategory(CATEGORY);
            hjp.setAgentChannel(AGENT_CHANNEL);
            hjp.setOrganCode(ORGAN_CODE);
            hjp.setData(qaReq.getTitle());
            String jsonBody = JSON.toJSONString(hjp);
            logger.info("当前请求样例id[{}]===参数[{}]",qaReq.getCaseId(),jsonBody);
            Map<String, Object> httpMap = httpPost(URL, jsonBody);
            //本地测试 //TODO
//            Map<String ,Object> httpMap = new HashMap();
//            httpMap.put("httpStatus","200");
//            httpMap.put("resultBody","{\"content\":\"月交系列产品是指：岁悦添富、鑫悦人生、附加悦越多、附加悦越稳。\"}");
            QaResp qaResp = WriteExcelParam(qaReq, httpMap);
            qaResp.setParamData(jsonBody);
            respList.add(qaResp);
        });
        EasyExcel.write(FILE_NAME,QaResp.class).sheet(6,"T_QA").doWrite(respList);

    }
    public QaResp WriteExcelParam(QaReq qaReq,Map<String ,Object> map){
        QaResp qaResp = new QaResp();
        BeanUtils.copyProperties(qaReq,qaResp);
        logger.info("执行id=={}",qaResp.getCaseId());
        qaResp.setInterfaceName("QA");
        qaResp.setUrl(URL);
        String httpStatus = (String) map.get("httpStatus");
        Map<String,Object> resultBody = JSON.parseObject((String) map.get("resultBody"),Map.class);
        logger.info("resultBody:{}",resultBody);
        qaResp.setIsSame(false);
        if(!"200".equals(httpStatus)){
            qaResp.setHttpStatus(httpStatus);
            qaResp.setResponse("http请求异常");
            return qaResp;
        }
        String statusCode = String.valueOf(resultBody.get("statusCode"));
        String success = String.valueOf(resultBody.get("success"));
        logger.info("statusCode[{}]===success[{}]",statusCode,success);
        List<Map<String,Object>> data =null;
        Map<String,Object> msgBody =null;
        String content = null;
        if(resultBody != null && resultBody.size()>0) {
            data = (List<Map<String,Object>>) resultBody.get("data");
            msgBody = (Map<String, Object>)  data.get(0).get("msg_body");
            logger.info("msgBody:{}",msgBody.toString());
            content = (String) msgBody.get("content");
            if ("true".equals(success) && "200".equals(statusCode)) {
                if(qaReq.getAnswer().trim().equals(content.trim())){
                    qaResp.setIsSame(true);
                }
            }
            qaResp.setResponse(content);
        }
        qaResp.setHttpStatus(httpStatus);
        return qaResp;
    }



    public Map<String,Object> httpPost(String url,String body){
        Map<String,Object> resultMap = new HashMap<>();
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body,"UTF-8"));
            post.addHeader("content-type","application/json");
            post.setConfig(RequestConfig.custom().setConnectionRequestTimeout(4000).setSocketTimeout(10000).setConnectionRequestTimeout(10000).build());

             httpResponse = httpClient.execute(post);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String resultJson = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            if(!(statusCode==200)){
                logger.info("出错原因是{}",resultJson);
            }
            logger.info("执行成功========[{}]",resultJson);

            resultMap.put("httpStatus",String.valueOf(statusCode));
            resultMap.put("resultBody",resultJson);

        }catch (Exception e){
            logger.info("发送http请求出现异常!",e);

        }finally {
            if(httpClient !=null){
                try {
                    httpClient.close();
                }catch (IOException e){
                    logger.info("关闭http客户端请求出现异常",e);
                }
            }
            if(httpResponse !=null){
                try {
                    httpResponse.close();
                }catch (IOException e){
                    logger.info("关闭相应出现异常",e);
                }
            }
        }
        return  resultMap;
    }
}
