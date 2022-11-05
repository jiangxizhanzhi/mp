package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/5
 */
@Slf4j
public class IxiaLoadClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaLoadClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void createLoad(CreateLoadData createLoadData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_LOAD, createLoadData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Create load response:{}", response);
    }

    public void saveLoad() {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_LOAD, null, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Save load response:{}", response);
    }

    public void strainLoad(BaseCreateData baseCreateData) {
        var response = ixiaHttpClient.ixiaPost(IxiaUrlConstant.STRAIN_LOAD, baseCreateData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Strain load response:{}", response);
    }

    public void addPhase(IxiaPhaseData phaseData) {
        var response = ixiaHttpClient.ixiaPut(IxiaUrlConstant.ADD_PHASE, phaseData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add phase response:{}", response);
    }

    public void deletePhase() {
        var response = ixiaHttpClient.ixiaDelete(IxiaUrlConstant.DELETE_PHASE, null, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Delete phase response:{}", response);
    }

    public void changePhase(IxiaPhaseData phaseData, Integer index) {
        var response = ixiaHttpClient.ixiaPatch(IxiaUrlConstant.CHANGE_PHASE + index + "}", phaseData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Change phase response:{}", response);
    }
}
