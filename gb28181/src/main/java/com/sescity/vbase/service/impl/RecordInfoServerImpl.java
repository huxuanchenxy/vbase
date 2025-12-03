package com.sescity.vbase.service.impl;

import com.sescity.vbase.service.IRecordInfoServer;
import com.sescity.vbase.storager.dao.RecordInfoDao;
import com.sescity.vbase.storager.dao.dto.RecordInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordInfoServerImpl implements IRecordInfoServer {

    @Autowired
    private RecordInfoDao recordInfoDao;

    @Override
    public PageInfo<RecordInfo> getRecordList(int page, int count) {
        PageHelper.startPage(page, count);
        List<RecordInfo> all = recordInfoDao.selectAll();
        return new PageInfo<>(all);
    }
}
