package com.sescity.vbase.media.zlm.dto;

import java.text.ParseException;

public interface ChannelOnlineEvent {

    void run(String app, String stream, String serverId) throws ParseException;
}
