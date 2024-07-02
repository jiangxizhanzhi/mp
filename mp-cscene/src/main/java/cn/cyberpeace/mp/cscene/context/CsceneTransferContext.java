package cn.cyberpeace.mp.cscene.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CsceneTransferContext implements TransferContext<Map<String, String>> {

    public static final String CALL_ACTION_KEY = "CALL_ACTION";

    public static final String CSCENE_INSTANCE_ID_KEY = "CSCENE_INSTANCE_ID";

    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "CSCENE";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public Map<String, String> getValue() {
        Map<String, String> value = new HashMap<>();
        value.put(CALL_ACTION_KEY, CsceneContextHolder.getCallAction());
        value.put(CSCENE_INSTANCE_ID_KEY, CsceneContextHolder.getCsceneInstanceId());
        return value;
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(Map<String, String> value) {
        CsceneContextHolder.setCallAction(value.getOrDefault(CALL_ACTION_KEY, null));
        CsceneContextHolder.setCsceneInstanceId(value.getOrDefault(CSCENE_INSTANCE_ID_KEY, null));
    }
}
