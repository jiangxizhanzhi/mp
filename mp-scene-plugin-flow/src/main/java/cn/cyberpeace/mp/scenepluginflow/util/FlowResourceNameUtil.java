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
    @Override
    public String generate(String id, RsourceNameParams params) {
        return StringUtils.joinWith("_", FlowPlatformEnum.TREX, id);
    }

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