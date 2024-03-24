package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import com.fasterxml.jackson.core.type.TypeReference;


public class BroadCaseSaveSceneEvent extends MultiSceneBaseEvent<CsceneBO, ServiceResult<CsceneBO>> {
    public BroadCaseSaveSceneEvent(CsceneBO message) {
        super(MultiSceneBaseEvent.BROADCAST_SAVE_SCENE_CONFIG, message);
    }

    @Override
    public void doValidateReturnMessage(ServiceResult<CsceneBO> returnData) {
        if (!returnData.isSuccess()) {
            throw new MpRuntimeException(String.format("发送%s失败", this.getClass().getSimpleName()));
        }
    }


    @Override
    public TypeReference<ServiceResult<CsceneBO>> doGetReturnTypeReference() {
        return new TypeReference<ServiceResult<CsceneBO>>() {
        };
    }

}
