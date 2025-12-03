package com.sescity.vbase.gb28181.transmit.event.response;

import javax.sip.ResponseEvent;

/**    
 * @description:处理接收IPCamera发来的SIP协议响应消息
 */
public interface ISIPResponseProcessor {

	void process(ResponseEvent evt);


}
