package com.sescity.vbase.storager.dao;

import com.sescity.vbase.gb28181.bean.MonitorServer;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 服务器监控的设备
 */
@Mapper
@Repository
public interface MonitorServerMapper {

    @Select("SELECT * FROM monitor_server")
    List<MonitorServer> getMonitorServer();
}
