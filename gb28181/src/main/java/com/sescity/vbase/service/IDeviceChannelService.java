package com.sescity.vbase.service;

import com.sescity.vbase.gb28181.bean.Device;
import com.sescity.vbase.gb28181.bean.DeviceChannel;
import com.sescity.vbase.vmanager.bean.ResourceBaceInfo;

import java.util.List;

/**
 * 国标通道业务类
 */
public interface IDeviceChannelService {

    /**
     * 更新gps信息
     */
    DeviceChannel updateGps(DeviceChannel deviceChannel, Device device);

    /**
     * 添加设备通道
     *
     * @param deviceId 设备id
     * @param channel 通道
     */
    void updateChannel(String deviceId, DeviceChannel channel);

    /**
     * 批量添加设备通道
     *
     * @param deviceId 设备id
     * @param channels 多个通道
     */
    int updateChannels(String deviceId, List<DeviceChannel> channels);

    /**
     * 获取统计信息
     * @return
     */
    ResourceBaceInfo getOverview();
}
