package com.sescity.vbase.conf;

import com.alibaba.fastjson.JSONObject;
import com.sescity.vbase.service.IMediaServerService;
import com.sescity.vbase.storager.IRedisCacheStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VbaseTimerTask {

    @Autowired
    private IRedisCacheStorage redisCatchStorage;

    @Autowired
    private IMediaServerService mediaServerService;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private SipConfig sipConfig;

    @Scheduled(fixedRate = 2 * 1000)   //每3秒执行一次
    public void execute(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", sipConfig.getIp());
        jsonObject.put("port", serverPort);
        redisCatchStorage.updateVbaseInfo(jsonObject, 3);
    }
}
