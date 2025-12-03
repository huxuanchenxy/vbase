package com.sescity.vbase.gb28181.transmit.event.request.impl.message.response.cmd;

import com.sescity.vbase.gb28181.bean.*;
import com.sescity.vbase.gb28181.session.CatalogDataCache;
import com.sescity.vbase.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.sescity.vbase.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.sescity.vbase.gb28181.transmit.event.request.impl.message.response.ResponseMessageHandler;
import com.sescity.vbase.gb28181.utils.XmlUtil;
import com.sescity.vbase.storager.IVideoManagerStorage;
import gov.nist.javax.sip.message.SIPRequest;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 目录查询的回复
 */
@Component
public class CatalogResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(CatalogResponseMessageHandler.class);
    private final String cmdType = "Catalog";

    private boolean taskQueueHandlerRun = false;

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    private ConcurrentLinkedQueue<HandlerCacheData> taskQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private CatalogDataCache catalogDataCache;

    @Qualifier("taskExecutor")
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {
        taskQueue.offer(new HandlerCacheData(evt, device, element));
        // 回复200 OK
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            logger.error("[命令发送失败] 目录查询回复: {}", e.getMessage());
        }
        if (!taskQueueHandlerRun) {
            taskQueueHandlerRun = true;
            taskExecutor.execute(() -> {
                while (!taskQueue.isEmpty()) {
                    HandlerCacheData take = taskQueue.poll();
                    Element rootElement = null;
                    try {
                        rootElement = getRootElement(take.getEvt(), take.getDevice().getCharset());
                    } catch (DocumentException e) {
                        logger.error("[xml解析] 失败： ", e);
                        continue;
                    }
                    if (rootElement == null) {
                        logger.warn("[ 收到通道 ] content cannot be null, {}", evt.getRequest());
                        continue;
                    }
                    Element deviceListElement = rootElement.element("DeviceList");
                    Element sumNumElement = rootElement.element("SumNum");
                    Element snElement = rootElement.element("SN");
                    int sumNum = Integer.parseInt(sumNumElement.getText());

                    if (sumNum == 0) {
                        logger.info("[收到通道]设备:{}的: 0个", take.getDevice().getDeviceId());
                        // 数据已经完整接收
                        storager.cleanChannelsForDevice(take.getDevice().getDeviceId());
                        catalogDataCache.setChannelSyncEnd(take.getDevice().getDeviceId(), null);
                    } else {
                        Iterator<Element> deviceListIterator = deviceListElement.elementIterator();
                        if (deviceListIterator != null) {
                            List<DeviceChannel> channelList = new ArrayList<>();
                            // 遍历DeviceList
                            while (deviceListIterator.hasNext()) {
                                Element itemDevice = deviceListIterator.next();
                                Element channelDeviceElement = itemDevice.element("DeviceID");
                                if (channelDeviceElement == null) {
                                    continue;
                                }
                                DeviceChannel deviceChannel = XmlUtil.channelContentHander(itemDevice, device, null);
                                deviceChannel.setDeviceId(take.getDevice().getDeviceId());

                                channelList.add(deviceChannel);
                            }
                            int sn = Integer.parseInt(snElement.getText());
                            catalogDataCache.put(take.getDevice().getDeviceId(), sn, sumNum, take.getDevice(), channelList);
                            logger.info("[收到通道]设备: {} -> {}个，{}/{}", take.getDevice().getDeviceId(), channelList.size(), catalogDataCache.get(take.getDevice().getDeviceId()) == null ? 0 : catalogDataCache.get(take.getDevice().getDeviceId()).size(), sumNum);
                            if (catalogDataCache.get(take.getDevice().getDeviceId()).size() == sumNum) {
                                // 数据已经完整接收， 此时可能存在某个设备离线变上线的情况，但是考虑到性能，此处不做处理，
                                // 目前支持设备通道上线通知时和设备上线时向上级通知
                                boolean resetChannelsResult = storager.resetChannels(take.getDevice().getDeviceId(), catalogDataCache.get(take.getDevice().getDeviceId()));
                                if (!resetChannelsResult) {
                                    String errorMsg = "接收成功，写入失败，共" + sumNum + "条，已接收" + catalogDataCache.get(take.getDevice().getDeviceId()).size() + "条";
                                    catalogDataCache.setChannelSyncEnd(take.getDevice().getDeviceId(), errorMsg);
                                } else {
                                    catalogDataCache.setChannelSyncEnd(take.getDevice().getDeviceId(), null);
                                }
                            }
                        }

                    }
                }
                taskQueueHandlerRun = false;
            });
        }

    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {

    }

    public SyncStatus getChannelSyncProgress(String deviceId) {
        if (catalogDataCache.get(deviceId) == null) {
            return null;
        } else {
            return catalogDataCache.getSyncStatus(deviceId);
        }
    }

    public boolean isSyncRunning(String deviceId) {
        if (catalogDataCache.get(deviceId) == null) {
            return false;
        } else {
            return catalogDataCache.isSyncRunning(deviceId);
        }
    }

    public void setChannelSyncReady(Device device, int sn) {
        catalogDataCache.addReady(device, sn);
    }

    public void setChannelSyncEnd(String deviceId, String errorMsg) {
        catalogDataCache.setChannelSyncEnd(deviceId, errorMsg);
    }
}
