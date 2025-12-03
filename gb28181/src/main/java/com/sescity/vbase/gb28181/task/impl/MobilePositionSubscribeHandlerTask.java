package com.sescity.vbase.gb28181.task.impl;

import com.sescity.vbase.gb28181.task.ISubscribeTask;
import com.sescity.vbase.service.IPlatformService;
import com.sescity.vbase.utils.SpringBeanFactory;

/**
 * 向已经订阅(移动位置)的上级发送MobilePosition消息
 */
public class MobilePositionSubscribeHandlerTask implements ISubscribeTask {


    private IPlatformService platformService;
    private String platformId;


    public MobilePositionSubscribeHandlerTask(String platformId) {
        this.platformService = SpringBeanFactory.getBean("platformServiceImpl");
        this.platformId = platformId;
    }

    @Override
    public void run() {
        platformService.sendNotifyMobilePosition(this.platformId);
    }

    @Override
    public void stop() {

    }
}
