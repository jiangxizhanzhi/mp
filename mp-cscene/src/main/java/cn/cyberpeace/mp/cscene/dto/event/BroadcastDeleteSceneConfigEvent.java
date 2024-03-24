package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.CsceneActionRequest;
import com.fasterxml.jackson.core.type.TypeReference;

public class BroadcastDeleteSceneConfigEvent extends MultiSceneBaseEvent<CsceneActionRequest, ServiceResult<Boolean>> {
    public BroadcastDeleteSceneConfigEvent(CsceneActionRequest message) {
        super(BROADCAST_DELETE_SCENE_CONFIG, message);
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
