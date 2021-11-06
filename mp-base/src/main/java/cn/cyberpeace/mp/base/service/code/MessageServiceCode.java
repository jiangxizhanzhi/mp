package cn.cyberpeace.mp.base.service.code;

import cn.cyberpeace.mp.base.util.MessageUtils;
import lombok.Getter;

public class MessageServiceCode implements ServiceCode {

    @Getter
    private final ServiceCode serviceCode;

    @Getter
    private final String[] params;

    public MessageServiceCode(ServiceCode serviceCode, String... params) {
        this.serviceCode = serviceCode;
        this.params = params;
    }

    @Override
    public InnerCode getInnerCode() {
        return serviceCode.getInnerCode();
    }

    public String generateMessage() {
        if (params == null || params.length == 0) {
            return MessageUtils.get(getMessage());
        } else {
            return MessageUtils.get(getMessage(), params);
        }
    }

}
