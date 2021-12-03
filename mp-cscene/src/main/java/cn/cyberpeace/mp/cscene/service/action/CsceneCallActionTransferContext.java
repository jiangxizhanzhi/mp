package cn.cyberpeace.mp.cscene.service.action;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CsceneCallActionTransferContext implements TransferContext<String> {
    @Override
    public String getKey() {
        return "CSCENE_CALL_ACTION";
    }

    @Override
    public String getValue() {
        return CsceneContextHolder.getCallAction();
    }

    @Override
    public void setValue(String value) {
        CsceneContextHolder.setCallAction(value);
    }
}
