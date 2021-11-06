package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    @Override
    public String getKey() {
        return "CALL_CHAIN_ID";
    }

    @Override
    public String getValue() {
        return SysContextHolder.getCallChainId();
    }

    @Override
    public void setValue(String value) {
        SysContextHolder.setCallChainId(value);
    }
}
