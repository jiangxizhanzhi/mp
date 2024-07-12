 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneTransferContext.java.
 // File size 1040.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SceneTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class SceneTransferContext implements TransferContext<Map<String, String>> {

    private static final String CSCENE_INSTANCE_ID_KEY = "CSCENE_INSTANCE_ID_KEY";
    @Override
    public String getKey() {
        return "SCENE";
    }

    @Override
    public Map<String, String> getValue() {
        Map<String, String> value = new HashMap<>();
        value.put(CSCENE_INSTANCE_ID_KEY, SceneContextHolder.getCsceneInstanceId());
        return value;
    }

    @Override
    public void setValue(Map<String, String> value) {
        SceneContextHolder.setCsceneInstanceId(value.getOrDefault(CSCENE_INSTANCE_ID_KEY, null));

    }
}
