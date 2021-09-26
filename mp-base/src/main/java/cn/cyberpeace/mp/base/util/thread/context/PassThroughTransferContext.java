package cn.cyberpeace.mp.base.util.thread.context;

import cn.cyberpeace.mp.base.context.SysContextUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PassThroughTransferContext implements TransferContext<Map<String, Object>> {
    @Override
    public Map<String, Object> getValue() {
        return SysContextUtils.getPassThroughMap();
    }

    @Override
    public void setValue(Map<String, Object> value) {
        SysContextUtils.setPassThroughMap(value);
    }
}
