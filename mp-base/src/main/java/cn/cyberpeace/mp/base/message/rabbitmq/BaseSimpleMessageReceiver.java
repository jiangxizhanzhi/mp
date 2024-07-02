package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.util.Map;

/**
 * @ClassName BaseSimpleMessageReceiver
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseMessageReceiver
 */
@Slf4j
public abstract class BaseSimpleMessageReceiver extends BaseMessageReceiver {

    /**
     * BaseSimpleMessageReceiver
     *
     * @param messageConverter
     */
    protected BaseSimpleMessageReceiver(MessageConverter messageConverter) {
        setMessageConverter(messageConverter);
    }

    /**
     * receive
     *
     * @param message
     */
    @Override
    public void receive(Message message) {
        FormatMessage formatMessage = parseMessage(message, FormatMessage.class);
        if (formatMessage == null) {
            return;
        }

        Map context = formatMessage.getContext();
        if (context != null) {
            TransferContext.setContextMap(context);
        }
        handleMessage(formatMessage);
    }

    /**
     * handleMessage
     *
     * @param formatMessage
     */
    public abstract void handleMessage(FormatMessage<?> formatMessage);

    /**
     * toFormatMessage
     *
     * @param formatMessage
     * @return
     */
    public <T> FormatMessage<T> toFormatMessage(FormatMessage<?> formatMessage) {
        T data = null;
        try {
            data = (T) formatMessage.getData();
        } catch (Exception e) {
            log.error("Parse format message[{}] raw data error: {}", formatMessage, e);
        }

        return new FormatMessage<>(data, formatMessage.getType(), formatMessage.getContext());
    }

    /**
     * toFormatMessage
     *
     * @param formatMessage
     * @param dataClass
     * @return
     */
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
