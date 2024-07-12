 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowResourceNameUtil.java.
 // File size 1355.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.util;

import cn.cyberpeace.mp.flow.enums.FlowPlatformEnum;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.ResourceParseWrapper;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
@Component
public class FlowResourceNameUtil implements IFlowResourceName<RsourceNameParams> {
    /**
     * generate
     *
     * @param id
     * @param params
     * @return
     */
    @Override
    public String generate(String id, RsourceNameParams params) {
        return StringUtils.joinWith("_", FlowPlatformEnum.TREX, id);
    }

    /**
     * parse
     *
     * @param name
     * @return
     */
    @Override
    public ResourceParseWrapper<RsourceNameParams> parse(String name) {
        if (StringUtils.isNotEmpty(name) && name.startsWith(FlowPlatformEnum.TREX.toString())) {
            String id = StringUtils.replace(name, FlowPlatformEnum.TREX + "_", "");
            RsourceNameParams params = new RsourceNameParams();
            params.setSceneId(id);
            return new ResourceParseWrapper<>(params);
        }
        return new ResourceParseWrapper<>();
    }
}