package cn.cyberpeace.mp.data.sandtable;

import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 调用底层的服务中转
 */
@Service
@Slf4j
public class SceneTransformServiceImpl {


    @Value("mp.data.sandTable.sceneBaseUrl")
    private String sceneBaseUrl;

    /**
     * 查询底层所有场景数据
     * @return≠
     */
    public String queryAllScene(){
        if (ObjectUtils.isEmpty(sceneBaseUrl)) {
            throw new MqRuntimeException("底层场景服务url不存在，请配置");
        }
        String sceneListString;
        try {
            sceneListString = HttpUtils.sendGet(sceneBaseUrl);
        } catch (IOException e) {
            log.error("查询所有场景错误:"+ e);
            throw new MqRuntimeException("查询场景错误:"+ e);
        }

        return sceneListString;
    }
}
