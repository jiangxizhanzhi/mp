package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

/**
 * @ClassName CallChainIdTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "CALL_CHAIN_ID";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public String getValue() {
        return SysContextHolder.getCallChainId();
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(String value) {
        SysContextHolder.setCallChainId(value);
    }
}
