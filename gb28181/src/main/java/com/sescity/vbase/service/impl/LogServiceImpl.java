package com.sescity.vbase.service.impl;

import com.sescity.vbase.service.ILogService;
import com.sescity.vbase.storager.dao.LogMapper;
import com.sescity.vbase.storager.dao.dto.LogDto;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements ILogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageInfo<LogDto> getAll(int page, int count, String query, String type, String startTime, String endTime) {
        PageHelper.startPage(page, count);
        List<LogDto> all = logMapper.query(query, type, startTime, endTime);
        return new PageInfo<>(all);
    }

    @Override
    public void add(LogDto logDto) {
        logMapper.add(logDto);
    }

    @Override
    public int clear() {
        return logMapper.clear();
    }
}
