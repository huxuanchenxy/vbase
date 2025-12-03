package com.sescity.vbase.vmanager.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sescity.vbase.VbaseBootstrap;
import com.sescity.vbase.common.SystemAllInfo;
import com.sescity.vbase.common.VersionPo;
import com.sescity.vbase.conf.SipConfig;
import com.sescity.vbase.conf.UserSetting;
import com.sescity.vbase.conf.VersionInfo;
import com.sescity.vbase.conf.exception.ControllerException;
import com.sescity.vbase.gb28181.bean.MonitorServer;
import com.sescity.vbase.media.zlm.ZlmHttpHookSubscribe;
import com.sescity.vbase.media.zlm.dto.IHookSubscribe;
import com.sescity.vbase.media.zlm.dto.MediaServerItem;
import com.sescity.vbase.service.*;
import com.sescity.vbase.service.bean.MediaServerLoad;
import com.sescity.vbase.storager.IRedisCacheStorage;
import com.sescity.vbase.utils.SpringBeanFactory;
import com.sescity.vbase.vmanager.bean.ErrorCode;
import com.sescity.vbase.vmanager.bean.ResourceBaceInfo;
import com.sescity.vbase.vmanager.bean.ResourceInfo;
import com.sescity.vbase.vmanager.bean.SystemConfigInfo;
import gov.nist.javax.sip.SipStackImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.SipProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
@Tag(name = "服务控制")
@CrossOrigin
@RestController
@RequestMapping("/api/server")
public class ServerController implements ApplicationContextAware {

    @Autowired
    private ZlmHttpHookSubscribe zlmHttpHookSubscribe;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private VersionInfo versionInfo;

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceChannelService channelService;

    @Autowired
    private IStreamPushService pushService;


    @Autowired
    private IStreamProxyService proxyService;

    @Autowired
    private IMonitorServerService monitorServerService;

    @Value("${server.port}")
    private int serverPort;


    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private IRedisCacheStorage redisCatchStorage;

    private ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.context = applicationContext;
    }


    @GetMapping(value = "/media_server/list")
    @ResponseBody
    @Operation(summary = "流媒体服务列表")
    public List<MediaServerItem> getMediaServerList() {
        return mediaServerService.getAll();
    }

    @GetMapping(value = "/media_server/online/list")
    @ResponseBody
    @Operation(summary = "在线流媒体服务列表")
    public List<MediaServerItem> getOnlineMediaServerList() {
        return mediaServerService.getAllOnline();
    }

    @GetMapping(value = "/media_server/one/{id}")
    @ResponseBody
    @Operation(summary = "停止视频回放")
    @Parameter(name = "id", description = "流媒体服务ID", required = true)
    public MediaServerItem getMediaServer(@PathVariable String id) {
        return mediaServerService.getOne(id);
    }

    @Operation(summary = "测试流媒体服务")
    @Parameter(name = "ip", description = "流媒体服务IP", required = true)
    @Parameter(name = "port", description = "流媒体服务HTT端口", required = true)
    @Parameter(name = "secret", description = "流媒体服务secret", required = true)
    @GetMapping(value = "/media_server/check")
    @ResponseBody
    public MediaServerItem checkMediaServer(@RequestParam String ip, @RequestParam int port, @RequestParam String secret) {
        return mediaServerService.checkMediaServer(ip, port, secret);
    }

    @Operation(summary = "测试流媒体录像管理服务")
    @Parameter(name = "ip", description = "流媒体服务IP", required = true)
    @Parameter(name = "port", description = "流媒体服务HTT端口", required = true)
    @GetMapping(value = "/media_server/record/check")
    @ResponseBody
    public void checkMediaRecordServer(@RequestParam String ip, @RequestParam int port) {
        boolean checkResult = mediaServerService.checkMediaRecordServer(ip, port);
        if (!checkResult) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "连接失败");
        }
    }

    @Operation(summary = "保存流媒体服务")
    @Parameter(name = "mediaServerItem", description = "流媒体信息", required = true)
    @PostMapping(value = "/media_server/save")
    @ResponseBody
    public void saveMediaServer(@RequestBody MediaServerItem mediaServerItem) {
        MediaServerItem mediaServerItemInDatabase = mediaServerService.getOne(mediaServerItem.getId());

        if (mediaServerItemInDatabase != null) {
            if (ObjectUtils.isEmpty(mediaServerItemInDatabase.getSendRtpPortRange()) && ObjectUtils.isEmpty(mediaServerItem.getSendRtpPortRange())) {
                mediaServerItem.setSendRtpPortRange("30000,30500");
            }
            mediaServerService.update(mediaServerItem);
        } else {
            if (ObjectUtils.isEmpty(mediaServerItem.getSendRtpPortRange())) {
                mediaServerItem.setSendRtpPortRange("30000,30500");
            }
            mediaServerService.add(mediaServerItem);
        }
    }

    @Operation(summary = "移除流媒体服务")
    @Parameter(name = "id", description = "流媒体ID", required = true)
    @DeleteMapping(value = "/media_server/delete")
    @ResponseBody
    public void deleteMediaServer(@RequestParam String id) {
        if (mediaServerService.getOne(id) == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "未找到此节点");
        }
        mediaServerService.delete(id);
        mediaServerService.deleteDb(id);
    }


    @Operation(summary = "重启服务")
    @GetMapping(value = "/restart")
    @ResponseBody
    public void restart() {
        taskExecutor.execute(()-> {
            try {
                Thread.sleep(3000);
                SipProvider up = (SipProvider) SpringBeanFactory.getBean("udpSipProvider");
                SipStackImpl stack = (SipStackImpl) up.getSipStack();
                stack.stop();
                Iterator listener = stack.getListeningPoints();
                while (listener.hasNext()) {
                    stack.deleteListeningPoint((ListeningPoint) listener.next());
                }
                Iterator providers = stack.getSipProviders();
                while (providers.hasNext()) {
                    stack.deleteSipProvider((SipProvider) providers.next());
                }
                VbaseBootstrap.restart();
            } catch (InterruptedException | ObjectInUseException e) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
            }
        });
    };

    @Operation(summary = "获取系统信息信息")
    @GetMapping(value = "/system/configInfo")
    @ResponseBody
    public SystemConfigInfo getConfigInfo() {
        SystemConfigInfo systemConfigInfo = new SystemConfigInfo();
        systemConfigInfo.setVersion(versionInfo.getVersion());
        systemConfigInfo.setSip(sipConfig);
        systemConfigInfo.setAddOn(userSetting);
        systemConfigInfo.setServerPort(serverPort);
        return systemConfigInfo;
    }

    @Operation(summary = "获取版本信息")
    @GetMapping(value = "/version")
    @ResponseBody
    public VersionPo VersionPogetVersion() {
        return versionInfo.getVersion();
    }

    @GetMapping(value = "/config")
    @Operation(summary = "获取配置信息")
    @Parameter(name = "type", description = "配置类型（sip, base）", required = true)
    @ResponseBody
    public JSONObject getVersion(String type) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server.port", serverPort);
        if (ObjectUtils.isEmpty(type)) {
            jsonObject.put("sip", JSON.toJSON(sipConfig));
            jsonObject.put("base", JSON.toJSON(userSetting));
        } else {
            switch (type) {
                case "sip":
                    jsonObject.put("sip", sipConfig);
                    break;
                case "base":
                    jsonObject.put("base", userSetting);
                    break;
                default:
                    break;
            }
        }
        return jsonObject;
    }

    @GetMapping(value = "/hooks")
    @ResponseBody
    @Operation(summary = "获取当前所有hook")
    public List<IHookSubscribe> getHooks() {
        return zlmHttpHookSubscribe.getAll();
    }

    @GetMapping(value = "/system/info")
    @ResponseBody
    @Operation(summary = "获取系统信息")
    public SystemAllInfo getSystemInfo() {
        SystemAllInfo systemAllInfo = redisCatchStorage.getSystemInfo();

        return systemAllInfo;
    }

    @GetMapping(value = "/media_server/load")
    @ResponseBody
    @Operation(summary = "获取负载信息")
    public List<MediaServerLoad> getMediaLoad() {
        List<MediaServerLoad> result = new ArrayList<>();
        List<MediaServerItem> allOnline = mediaServerService.getAllOnline();
        if (allOnline.size() == 0) {
            return result;
        }else {
            for (MediaServerItem mediaServerItem : allOnline) {
                result.add(mediaServerService.getLoad(mediaServerItem));
            }
        }
        return result;
    }

    @GetMapping(value = "/resource/info")
    @ResponseBody
    @Operation(summary = "获取负载信息")
    public ResourceInfo getResourceInfo() {
        ResourceInfo result = new ResourceInfo();
        ResourceBaceInfo deviceInfo = deviceService.getOverview();
        result.setDevice(deviceInfo);
        ResourceBaceInfo channelInfo = channelService.getOverview();
        result.setChannel(channelInfo);
        ResourceBaceInfo pushInfo = pushService.getOverview();
        result.setPush(pushInfo);
        ResourceBaceInfo proxyInfo = proxyService.getOverview();
        result.setProxy(proxyInfo);

        return result;
    }

    @GetMapping(value ="/monitor_server")
    @ResponseBody
    @Operation(summary = "获取所有相关的服务器信息")
    public List<MonitorServer> getAllMonitorServer() {
        return monitorServerService.getAllMonitorServer();
    }

    @PostMapping(value ="/save-and-restart")
    @Operation(summary = "更新配置并重启")
    public void SaveAndRestart(@RequestBody SipConfig sipConfig) throws IOException {
        if (sipConfig.getIp() == null)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "ip不能为空");

        if (!Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", sipConfig.getIp())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "ip不合法");
        }

        if (sipConfig.getPort() == null)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "port不能为空");

        if (sipConfig.getPort() < 0 || sipConfig.getPort() > 65535)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "port不合法");

        if (sipConfig.getPassword() == null)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "password不能为空");

        if (sipConfig.getId() == null)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "id不能为空");

        if (sipConfig.getDomain() == null)
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "domain不能为空");

        String src = Paths.get("", "application.yml").toAbsolutePath().toString();

        Yaml yaml = new Yaml();
        FileWriter fileWriter;
        FileInputStream fileInputStream = new FileInputStream(new File(src));
        Map<String, Object> yamlMap = yaml.load(fileInputStream);
        Map<String, Object> sipMap = (Map<String, Object>) yamlMap.get("sip");

        if (sipMap == null) {
            sipMap = new HashMap<>();
            yamlMap.put("sip", sipMap);
        }

        sipMap.put("ip", sipConfig.getIp());
        sipMap.put("port", sipConfig.getPort());
        sipMap.put("password", sipConfig.getPassword());
        sipMap.put("id", sipConfig.getId());
        sipMap.put("domain", sipConfig.getDomain());

        //字符输出
        fileWriter = new FileWriter(new File(src));
        //用yaml方法把map结构格式化为yaml文件结构
        fileWriter.write(yaml.dumpAsMap(yamlMap));
        //刷新
        fileWriter.flush();
        //关闭流
        fileWriter.close();
        fileInputStream.close();

        Runtime.getRuntime().exec("pm2 restart vbase");
    }
}
