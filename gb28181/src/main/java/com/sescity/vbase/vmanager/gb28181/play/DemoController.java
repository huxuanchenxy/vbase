package com.sescity.vbase.vmanager.gb28181.play;

import com.alibaba.fastjson.JSONObject;
import com.sescity.vbase.common.StreamInfo;
import com.sescity.vbase.conf.exception.ControllerException;
import com.sescity.vbase.conf.exception.SsrcTransactionNotFoundException;
import com.sescity.vbase.gb28181.bean.Device;
import com.sescity.vbase.gb28181.transmit.cmd.impl.SIPCommander;
import com.sescity.vbase.media.zlm.dto.MediaServerItem;
import com.sescity.vbase.service.IPlayService;
import com.sescity.vbase.storager.IRedisCacheStorage;
import com.sescity.vbase.storager.IVideoManagerStorage;
import com.sescity.vbase.vmanager.bean.ErrorCode;
import com.sescity.vbase.vmanager.bean.VbaseResult;
import com.sescity.vbase.vmanager.gb28181.play.bean.PlayResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

@Tag(name  = "国标设备点播")
@CrossOrigin
@RestController
@RequestMapping("/api/demo/play")
public class DemoController {

  private final static Logger logger = LoggerFactory.getLogger(PlayController.class);

  @Autowired
  private IVideoManagerStorage storager;

  @Autowired
  private IPlayService playService;

  @Autowired
  private IRedisCacheStorage redisCatchStorage;

  @Autowired
  private SIPCommander cmder;

  @Operation(summary = "开始点播")
  @Parameter(name = "deviceId", description = "设备国标编号", required = true)
  @Parameter(name = "channelId", description = "通道国标编号", required = true)
  @GetMapping("/start/{deviceId}/{channelId}")
  public DeferredResult<VbaseResult<StreamInfo>> play(@PathVariable String deviceId,
                                                      @PathVariable String channelId) {

    // 获取可用的zlm
    Device device = storager.queryVideoDevice(deviceId);
    MediaServerItem newMediaServerItem = playService.getNewMediaServerItem(device);
    PlayResult playResult = playService.play(newMediaServerItem, deviceId, channelId, null, null, null);

    return playResult.getResult();
  }

  @Operation(summary = "停止点播")
  @Parameter(name = "deviceId", description = "设备国标编号", required = true)
  @Parameter(name = "channelId", description = "通道国标编号", required = true)
  @GetMapping("/stop/{deviceId}/{channelId}")
  public JSONObject playStop(@PathVariable String deviceId, @PathVariable String channelId) {

    logger.debug(String.format("设备预览/回放停止API调用，streamId：%s_%s", deviceId, channelId ));

    if (deviceId == null || channelId == null) {
      throw new ControllerException(ErrorCode.ERROR400);
    }

    Device device = storager.queryVideoDevice(deviceId);
    if (device == null) {
      throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备[" + deviceId + "]不存在");
    }

    StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(deviceId, channelId);
    if (streamInfo == null) {
      throw new ControllerException(ErrorCode.ERROR100.getCode(), "点播未找到");
    }

    try {
      logger.warn("[停止点播] {}/{}", device.getDeviceId(), channelId);
      cmder.streamByeCmd(device, channelId, streamInfo.getStream(), null, null);
    } catch (InvalidArgumentException | SipException | ParseException | SsrcTransactionNotFoundException e) {
      logger.error("[命令发送失败] 停止点播， 发送BYE: {}", e.getMessage());
      throw new ControllerException(ErrorCode.ERROR100.getCode(), "命令发送失败: " + e.getMessage());
    }
    redisCatchStorage.stopPlay(streamInfo);

    storager.stopPlay(streamInfo.getDeviceID(), streamInfo.getChannelId());
    JSONObject json = new JSONObject();
    json.put("deviceId", deviceId);
    json.put("channelId", channelId);
    return json;

  }
}

