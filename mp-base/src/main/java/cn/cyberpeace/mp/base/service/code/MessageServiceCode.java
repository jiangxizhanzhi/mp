package cn.cyberpeace.mp.base.service.code;

import cn.cyberpeace.mp.base.common.MessageCommon;
import lombok.Getter;

/**
 * @ClassName MessageServiceCode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServiceCode
 */
public class MessageServiceCode implements ServiceCode {

    @Getter
    private final ServiceCode serviceCode;

    @Getter
    private final String[] params;

    /**
     * MessageServiceCode
     *
     * @param serviceCode
     * @param params
     */
    public MessageServiceCode(ServiceCode serviceCode, String... params) {
        this.serviceCode = serviceCode;
        this.params = params;
    }

    /**
     * getInnerCode
     *
     * @return
     */
    @Override
    public InnerCode getInnerCode() {
        return serviceCode.getInnerCode();
    }

    /**
     * transMessage
     *
     * @return
     */
    @Override
    public String transMessage() {
        if (params == null || params.length == 0) {
            return MessageCommon.get(getMessage());
        } else {
            return MessageCommon.get(getMessage(), params);
        }
    }

}
