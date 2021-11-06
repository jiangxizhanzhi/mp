package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
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

    public abstract void handleMessage(FormatMessage<?> formatMessage);

    public <T> FormatMessage<T> toFormatMessage(FormatMessage<?> formatMessage) {
        T data = null;
        try {
            data = (T) formatMessage.getData();
        } catch (Exception e) {
            log.error("Parse format message[{}] raw data error: {}", formatMessage, e);
        }

        return new FormatMessage<>(data, formatMessage.getType(), formatMessage.getContext());
    }

    public <T> FormatMessage<T> toFormatMessage(FormatMessage<?> formatMessage, Class<T> dataClass) {
        Map<String, Object> rawData = null;
        try {
            rawData = (Map<String, Object>) formatMessage.getData();
        } catch (Exception e) {
            log.error("Parse format message[{}] raw data error: {}", formatMessage, e);
        }

        T data = null;
        if (rawData != null) {
            data = JsonCommon.mapToBean(rawData, dataClass);
        }
        return new FormatMessage<>(data, formatMessage.getType(), formatMessage.getContext());
    }

}
