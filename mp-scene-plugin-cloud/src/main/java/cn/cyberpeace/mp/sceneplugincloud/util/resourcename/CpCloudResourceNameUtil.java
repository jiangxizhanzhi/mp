package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
@Component
public class CpCloudResourceNameUtil implements ICloudResourceName<RsourceNameParams> {
    /**
     * generate
     *
     * @param id
     * @param params
     * @return
     */
    @Override
    public String generate(String id, RsourceNameParams params) {
        if (params == null || params.getIndex() == null) {
            return StringUtils.joinWith("_", this.getPrefix(), id);
        } else {
            return StringUtils.joinWith("_", this.getPrefix(), id, params.getIndex());
        }
    }

    /**
     * parse
     *
     * @param name
     * @return
     */
    @Override
    public ResourceParseWrapper<RsourceNameParams> parse(String name) {
        if (StringUtils.isEmpty(name) || !name.startsWith(this.getPrefix())) {
            return new ResourceParseWrapper<>();
        }
        if (name.endsWith("collection")) {
            return new ResourceParseWrapper<>();
        }
        String sceneInfo = name.replace(this.getPrefix() + "_", "");
        String[] sp = sceneInfo.split("_");
        if (sp.length == 0) {
            return new ResourceParseWrapper<>();
        }
        RsourceNameParams result = new RsourceNameParams();
        result.setNodeId(sp[0]);
        if (sp.length > 1) {
            if (NumberUtil.isNumber(sp[1])) {
                result.setIndex(Integer.parseInt(sp[1]));
            }
        }
        return new ResourceParseWrapper<>(result);
    }

    /**
     * getPrefix
     *
     * @return
     */
    private String getPrefix() {
        return CloudSingleton.cloudAppConfig.getCpcloudResourceGroup();
    }
}
