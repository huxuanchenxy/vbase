package com.sescity.vbase.gb28181.session;

import com.sescity.vbase.utils.ConfigConst;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Schema(description = "ssrc信息")
public class SsrcConfig {

    @Schema(description = "流媒体服务器Id")
    private String mediaServerId;

    @Schema(description = "SSRC前缀")
    private String ssrcPrefix;

    /**
     * zlm流媒体服务器已用会话句柄
     */
    @Schema(description = "zlm流媒体服务器已用会话句柄")
    private List<Integer> isUsed;

    /**
     * zlm流媒体服务器可用会话句柄
     */
    @Schema(description = "zlm流媒体服务器可用会话句柄")
    private List<Integer> notUsed;

    public SsrcConfig() {
    }

    public SsrcConfig(String mediaServerId, Set<Integer> usedSet, String sipDomain) {
        this.mediaServerId = mediaServerId;
        this.isUsed = new ArrayList<>();
        this.ssrcPrefix = sipDomain.substring(3, 8);
        this.notUsed = new ArrayList<>();
        for (int i = 1; i < ConfigConst.MAX_STRTEAM_COUNT; i++) {
            if (null == usedSet || !usedSet.contains(i)) {
                this.notUsed.add(i);
            } else {
                this.isUsed.add(i);
            }
        }
    }


    /**
     * 获取视频预览的SSRC值,第一位固定为0
     * @return ssrc
     */
    public String getPlaySsrc() {
        return "0" + getSsrcPrefix() + getSN();
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     *
     */
    public String getPlayBackSsrc() {
        return "1" + getSsrcPrefix() + getSN();
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     * @param ssrc 需要重置的ssrc
     */
    public void releaseSsrc(String ssrc) {
        if (ssrc == null) {
            return;
        }
        Integer sn = Integer.parseInt(ssrc.substring(6));
        try {
            isUsed.remove(sn);
            notUsed.add(sn);
        }catch (NullPointerException e){
        }
    }

    /**
     * 获取后四位数SN,随机数
     *
     */
    private String getSN() {
        int sn;
        int index = 0;
        if (notUsed.size() == 0) {
            throw new RuntimeException("ssrc已经用完");
        } else if (notUsed.size() == 1) {
            sn = notUsed.get(0);
        } else {
            index = new Random().nextInt(notUsed.size() - 1);
            sn = notUsed.get(index);
        }
        notUsed.remove(index);
        isUsed.add(sn);
        return String.format("%04d", sn);
    }

    public String getSsrcPrefix() {
        return ssrcPrefix;
    }

    public String getMediaServerId() {
        return mediaServerId;
    }

    public void setMediaServerId(String mediaServerId) {
        this.mediaServerId = mediaServerId;
    }

    public void setSsrcPrefix(String ssrcPrefix) {
        this.ssrcPrefix = ssrcPrefix;
    }

    public List<Integer> getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(List<Integer> isUsed) {
        this.isUsed = isUsed;
    }

    public List<Integer> getNotUsed() {
        return notUsed;
    }

    public void setNotUsed(List<Integer> notUsed) {
        this.notUsed = notUsed;
    }

    public boolean checkSsrc(String ssrcInResponse) {
        return !isUsed.contains(ssrcInResponse);
    }
}
