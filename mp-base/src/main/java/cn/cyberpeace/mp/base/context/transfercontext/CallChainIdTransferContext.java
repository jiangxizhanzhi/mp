package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextUtils;
import org.springframework.stereotype.Component;

@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    @Override
    public String getKey() {
        return "CALL_CHAIN_ID";
    }

    @Override
    public String getValue() {
        return SysContextUtils.getCallChainId();
    }

    @Override
    public void setValue(String value) {
        SysContextUtils.setCallChainId(value);
    }
}
