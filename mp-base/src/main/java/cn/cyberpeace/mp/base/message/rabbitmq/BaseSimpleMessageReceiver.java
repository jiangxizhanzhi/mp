package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
import cn.cyberpeace.mp.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.util.Map;

@Slf4j
public abstract class BaseSimpleMessageReceiver extends BaseMessageReceiver {

    protected BaseSimpleMessageReceiver(MessageConverter messageConverter) {
        setMessageConverter(messageConverter);
    }

    @Override
    public void receive(Message message) {
        var formatMessage = parseMessage(message, FormatMessage.class);
        if (formatMessage == null) {
            return;
        }

        var context = formatMessage.getContext();
        if (context != null) {
            TransferContext.setContextMap(context);
        }
        handleMessage(formatMessage);
    }

    public abstract void handleMessage(FormatMessage<Map<String, Object>> formatMessage);

    public <T> FormatMessage<T> toFormatMessage(FormatMessage<Map<String, Object>> formatMessage, Class<T> dataClass) {
        Map<String, Object> rawData = formatMessage.getData();
        T data = null;
        if (rawData != null) {
            data = JsonUtils.mapToBean(rawData, dataClass);
        }
        return new FormatMessage<>(data, formatMessage.getType(), formatMessage.getContext());
    }

}
