package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;

public class CollectionSingleton {

    public static final CollectionAppConfig collectionAppConfig = BeanUtils.getBean(CollectionAppConfig.class);

    private CollectionSingleton() {}

}
