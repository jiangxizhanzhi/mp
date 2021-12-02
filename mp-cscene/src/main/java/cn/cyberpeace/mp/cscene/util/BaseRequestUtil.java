package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 09:34
 */
public class BaseRequestUtil {

    /**
     * 校验BaseRequest，没有sceneId和elementIds则报错
     *
     * @param baseRequest
     */
    public static void checkBaseRequestWithSceneIdAndElementIds(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException("sceneId为空");
        }

        List<String> elementIds = baseRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException("节点id不能为空");
        }
    }


    /**
     * 校验BaseRequest，没有sceneId则报错
     *
     * @param baseRequest
     */
    public static void checkBaseRequestWithSceneId(BaseSceneElementRequest baseRequest) {
        if (StringUtils.isBlank(baseRequest.getSceneId())) {
            throw new MpRuntimeException("sceneId为空");
        }
    }


}
