package com.sescity.vbase.gb28181.transmit.event.request;

import javax.sip.RequestEvent;

/**
 * @description: 对SIP事件进行处理，包括request， response， timeout， ioException, transactionTerminated,dialogTerminated
 */
public interface ISIPRequestProcessor {

	void process(RequestEvent event);

}
