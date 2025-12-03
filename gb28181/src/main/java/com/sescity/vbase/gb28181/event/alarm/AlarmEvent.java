package com.sescity.vbase.gb28181.event.alarm;

import com.sescity.vbase.gb28181.bean.DeviceAlarm;
import org.springframework.context.ApplicationEvent;

/**
 * @description: 报警事件
 */

public class AlarmEvent extends ApplicationEvent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AlarmEvent(Object source) {
        super(source);
    }

    private DeviceAlarm deviceAlarm;

    public DeviceAlarm getAlarmInfo() {
        return deviceAlarm;
    }
    
    public void setAlarmInfo(DeviceAlarm deviceAlarm) {
        this.deviceAlarm = deviceAlarm;
    }
}
