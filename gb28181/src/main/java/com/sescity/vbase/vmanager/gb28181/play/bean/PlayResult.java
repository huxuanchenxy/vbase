package com.sescity.vbase.vmanager.gb28181.play.bean;

import com.sescity.vbase.common.StreamInfo;
import com.sescity.vbase.gb28181.bean.Device;
import com.sescity.vbase.vmanager.bean.VbaseResult;
import org.springframework.web.context.request.async.DeferredResult;

public class PlayResult {

    private DeferredResult<VbaseResult<StreamInfo>> result;
    private String uuid;

    private Device device;

    public DeferredResult<VbaseResult<StreamInfo>> getResult() {
        return result;
    }

    public void setResult(DeferredResult<VbaseResult<StreamInfo>> result) {
        this.result = result;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
