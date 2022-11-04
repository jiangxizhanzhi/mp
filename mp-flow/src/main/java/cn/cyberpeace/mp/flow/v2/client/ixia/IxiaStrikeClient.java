package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/4
 */
@Slf4j
public class IxiaStrikeClient {
    private IxiaHttpClient ixiaHttpClient;

    public IxiaStrikeClient(IxiaHttpClient ixiaHttpClient) {
        this.ixiaHttpClient = ixiaHttpClient;
    }

    public void createStrikeList() {
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.CREATE_STRIKE_LIST, null, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Create strike list response:{}", result);
    }

    public void addStrikeToList(AddStrikeToListData addStrikeToListData) {
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.ADD_STRIKE_TO_LIST, addStrikeToListData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Add strike to list response:{}", result);
    }

    public void saveStrikeList(SaveStrikeListData saveStrikeListData) {
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.SAVE_STRIKE_LIST, saveStrikeListData, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Save strike list response:{}", result);
    }
}
