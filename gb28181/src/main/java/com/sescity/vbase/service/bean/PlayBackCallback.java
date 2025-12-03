package com.sescity.vbase.service.bean;

import com.sescity.vbase.gb28181.transmit.callback.RequestMessage;

public interface PlayBackCallback<T> {

    void call(PlayBackResult<T> msg);

}
