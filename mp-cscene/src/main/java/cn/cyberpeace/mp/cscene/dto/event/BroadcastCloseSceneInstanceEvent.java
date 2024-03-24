package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.CsceneInstanceActionRequest;
import com.fasterxml.jackson.core.type.TypeReference;

public class BroadcastCloseSceneInstanceEvent extends MultiSceneBaseEvent<CsceneInstanceActionRequest, ServiceResult<Boolean>>{
    public BroadcastCloseSceneInstanceEvent(CsceneInstanceActionRequest message) {
        super(BROADCAST_CLOSE_SCENE_INSTANCE, message);
    }

    @Override
    public void doValidateReturnMessage(ServiceResult<Boolean> returnData) {
        if (!returnData.isSuccess()) {
            throw new MpRuntimeException(String.format("发送%s失败", this.getClass().getSimpleName()));
        }
    }

    @Override
    public TypeReference<ServiceResult<Boolean>> doGetReturnTypeReference() {
        return new TypeReference<ServiceResult<Boolean>>() {
        };
    }
}
