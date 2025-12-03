package com.sescity.vbase.service.impl;

import com.sescity.vbase.gb28181.bean.MonitorServer;
import com.sescity.vbase.service.IMonitorServerService;
import com.sescity.vbase.storager.dao.MonitorServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorServerService implements IMonitorServerService {

  @Autowired
  private MonitorServerMapper monitorServerMapper;

  @Override
  public List<MonitorServer> getAllMonitorServer() {
    return monitorServerMapper.getMonitorServer();
  }
}
