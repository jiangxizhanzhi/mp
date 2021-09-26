package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

@Slf4j
public abstract class BaseSimpleMessageReceiver extends BaseMessageReceiver<FormatMessage> {

    @Override
    public void receive(Message message) {
        var formatMessage = parseMessage(message, FormatMessage.class);
        var context = formatMessage.getContext();
        if (context != null) {
            TransferContext.setContextMap(context);
        }
        handleMessage(formatMessage);
    }

}
