package com.sescity.vbase.gb28181.task;

import com.alibaba.fastjson.JSONObject;
import com.sescity.vbase.conf.UserSetting;
import com.sescity.vbase.gb28181.bean.Device;
import com.sescity.vbase.gb28181.bean.ParentPlatform;
import com.sescity.vbase.gb28181.bean.SendRtpItem;
import com.sescity.vbase.gb28181.transmit.cmd.ISIPCommanderForPlatform;
import com.sescity.vbase.media.zlm.ZLMRESTfulUtils;
import com.sescity.vbase.media.zlm.dto.MediaServerItem;
import com.sescity.vbase.service.IDeviceService;
import com.sescity.vbase.service.IMediaServerService;
import com.sescity.vbase.service.IPlatformService;
import com.sescity.vbase.storager.IRedisCacheStorage;
import com.sescity.vbase.storager.IVideoManagerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 系统启动时控制设备
 */
@Component
@Order(value=4)
public class SipRunner implements CommandLineRunner {

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IRedisCacheStorage redisCatchStorage;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ZLMRESTfulUtils zlmresTfulUtils;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IPlatformService platformService;

    @Autowired
    private ISIPCommanderForPlatform commanderForPlatform;

    @Override
    public void run(String... args) throws Exception {
        List<Device> deviceList = deviceService.getAllOnlineDevice();

        for (Device device : deviceList) {
            if (deviceService.expire(device)){
                deviceService.offline(device.getDeviceId());
            }else {
                deviceService.online(device);
            }
        }
        // 重置cseq计数
        redisCatchStorage.resetAllCSEQ();
        // 清理redis
        // 查找国标推流
        List<SendRtpItem> sendRtpItems = redisCatchStorage.queryAllSendRTPServer();
        if (sendRtpItems.size() > 0) {
            for (SendRtpItem sendRtpItem : sendRtpItems) {
                MediaServerItem mediaServerItem = mediaServerService.getOne(sendRtpItem.getMediaServerId());
                redisCatchStorage.deleteSendRTPServer(sendRtpItem.getPlatformId(),sendRtpItem.getChannelId(), sendRtpItem.getCallId(),sendRtpItem.getStreamId());
                if (mediaServerItem != null) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("vhost","__defaultVhost__");
                    param.put("app",sendRtpItem.getApp());
                    param.put("stream",sendRtpItem.getStreamId());
                    param.put("ssrc",sendRtpItem.getSsrc());
                    JSONObject jsonObject = zlmresTfulUtils.stopSendRtp(mediaServerItem, param);
                    if (jsonObject != null && jsonObject.getInteger("code") == 0) {
                        ParentPlatform platform = platformService.queryPlatformByServerGBId(sendRtpItem.getPlatformId());
                        if (platform != null) {
                            commanderForPlatform.streamByeCmd(platform, sendRtpItem.getCallId());
                        }
                    }
                }
            }
        }
    }
}
