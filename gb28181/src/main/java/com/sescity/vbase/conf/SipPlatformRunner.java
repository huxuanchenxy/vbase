package com.sescity.vbase.conf;

import com.sescity.vbase.gb28181.bean.ParentPlatform;
import com.sescity.vbase.gb28181.bean.ParentPlatformCatch;
import com.sescity.vbase.gb28181.transmit.cmd.ISIPCommanderForPlatform;
import com.sescity.vbase.service.IPlatformService;
import com.sescity.vbase.storager.IRedisCacheStorage;
import com.sescity.vbase.storager.IVideoManagerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(value=3)
public class SipPlatformRunner implements CommandLineRunner {

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IRedisCacheStorage redisCatchStorage;

    @Autowired
    private IPlatformService platformService;

    @Autowired
    private ISIPCommanderForPlatform sipCommanderForPlatform;


    @Override
    public void run(String... args) throws Exception {
        // 获取所有启用的平台
        List<ParentPlatform> parentPlatforms = storager.queryEnableParentPlatformList(true);

        for (ParentPlatform parentPlatform : parentPlatforms) {
            // 更新缓存
            ParentPlatformCatch parentPlatformCatch = new ParentPlatformCatch();
            parentPlatformCatch.setParentPlatform(parentPlatform);
            parentPlatformCatch.setId(parentPlatform.getServerGBId());
            redisCatchStorage.updatePlatformCatchInfo(parentPlatformCatch);
            // 设置所有平台离线
            platformService.offline(parentPlatform);
            // 取消订阅
            sipCommanderForPlatform.unregister(parentPlatform, null, (eventResult)->{
                platformService.login(parentPlatform);
            });
        }
    }
}
