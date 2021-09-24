package cn.cyberpeace.mp.base.util.thread.context;

import cn.cyberpeace.mp.base.context.SysContextUtils;
import org.springframework.stereotype.Component;

@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    @Override
    public String getValue() {
        return SysContextUtils.getCallChainId();
    }

    @Override
    public void setValue(String value) {
        SysContextUtils.setCallChainId(value);
    }
}
