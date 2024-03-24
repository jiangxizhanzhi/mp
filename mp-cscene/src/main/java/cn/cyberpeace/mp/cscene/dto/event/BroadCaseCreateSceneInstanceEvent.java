package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.controller.request.CsceneActionRequest;
import com.fasterxml.jackson.core.type.TypeReference;

public class BroadCaseCreateSceneInstanceEvent extends MultiSceneBaseEvent<CsceneActionRequest, ServiceResult<CsceneInstanceBO>> {

    public BroadCaseCreateSceneInstanceEvent(CsceneActionRequest message) {
        super(BROADCAST_START_SCENE_INSTANCE, message);
    }

    @Override
    public void doValidateReturnMessage(ServiceResult<CsceneInstanceBO> returnData) {
        if (!returnData.isSuccess()) {
            throw new MpRuntimeException(String.format("发送%s失败", this.getClass().getSimpleName()));
        }
    }

    @Override
    public TypeReference<ServiceResult<CsceneInstanceBO>> doGetReturnTypeReference() {
        return new TypeReference<ServiceResult<CsceneInstanceBO>>() {
        };
    }
}
