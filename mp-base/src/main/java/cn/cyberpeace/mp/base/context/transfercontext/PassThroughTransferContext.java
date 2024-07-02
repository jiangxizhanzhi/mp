package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName PassThroughTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class PassThroughTransferContext implements TransferContext<Map<String, Object>> {

    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "PASS_THROUGH";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public Map<String, Object> getValue() {
        return SysContextHolder.getPassThroughMap();
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(Map<String, Object> value) {
        SysContextHolder.setPassThroughMap(value);
    }
}
