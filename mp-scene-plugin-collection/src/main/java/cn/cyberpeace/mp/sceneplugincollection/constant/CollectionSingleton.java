package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;

public class CollectionSingleton {

    public static final CollectionAppConfig collectionAppConfig = BeanCommon.getBean(CollectionAppConfig.class);

    private CollectionSingleton() {}

}
