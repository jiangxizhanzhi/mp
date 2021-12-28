package cn.cyberpeace.mp.sceneplugincollection.plugin.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class HandlerUtils {

    public static String genResourceName(String name) {
        return StringUtils.joinWith("_",
                cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils.genResourceName(name), "collection");
    }

}
