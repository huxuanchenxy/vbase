package com.sescity.vbase.service;

import com.alibaba.fastjson.JSONObject;
import com.sescity.vbase.common.StreamInfo;
import com.sescity.vbase.conf.exception.ServiceException;
import com.sescity.vbase.gb28181.bean.Device;
import com.sescity.vbase.gb28181.bean.InviteStreamCallback;
import com.sescity.vbase.gb28181.bean.InviteStreamInfo;
import com.sescity.vbase.gb28181.event.SipSubscribe;
import com.sescity.vbase.media.zlm.ZlmHttpHookSubscribe;
import com.sescity.vbase.media.zlm.dto.MediaServerItem;
import com.sescity.vbase.service.bean.InviteTimeOutCallback;
import com.sescity.vbase.service.bean.PlayBackCallback;
import com.sescity.vbase.service.bean.SSRCInfo;
import com.sescity.vbase.vmanager.bean.VbaseResult;
import com.sescity.vbase.vmanager.gb28181.play.bean.PlayResult;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * 点播处理
 */
public interface IPlayService {

    void onPublishHandlerForPlay(MediaServerItem mediaServerItem, JSONObject resonse, String deviceId, String channelId, String uuid);

    void play(MediaServerItem mediaServerItem, SSRCInfo ssrcInfo, Device device, String channelId,
              ZlmHttpHookSubscribe.Event hookEvent, SipSubscribe.Event errorEvent,
              InviteTimeOutCallback timeoutCallback, String uuid);
    PlayResult play(MediaServerItem mediaServerItem, String deviceId, String channelId, ZlmHttpHookSubscribe.Event event, SipSubscribe.Event errorEvent, Runnable timeoutCallback);

    MediaServerItem getNewMediaServerItem(Device device);

    void onPublishHandlerForDownload(InviteStreamInfo inviteStreamInfo, String deviceId, String channelId, String toString);

    void playBack(String deviceId, String channelId, String startTime, String endTime, InviteStreamCallback infoCallBack, PlayBackCallback hookCallBack);
    void playBack(MediaServerItem mediaServerItem, SSRCInfo ssrcInfo, String deviceId, String channelId, String startTime, String endTime, InviteStreamCallback infoCallBack, PlayBackCallback hookCallBack);

    void zlmServerOffline(String mediaServerId);

    void download(String deviceId, String channelId, String startTime, String endTime, int downloadSpeed, InviteStreamCallback infoCallBack, PlayBackCallback hookCallBack);
    void download(MediaServerItem mediaServerItem, SSRCInfo ssrcInfo, String deviceId, String channelId, String startTime, String endTime, int downloadSpeed, InviteStreamCallback infoCallBack, PlayBackCallback hookCallBack);

    StreamInfo getDownLoadInfo(String deviceId, String channelId, String stream);

    void zlmServerOnline(String mediaServerId);

    void pauseRtp(String streamId) throws ServiceException, InvalidArgumentException, ParseException, SipException;

    void resumeRtp(String streamId) throws ServiceException, InvalidArgumentException, ParseException, SipException;
}
