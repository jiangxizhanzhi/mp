package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import org.springframework.stereotype.Component;

@Component
public class CollectionSingleton {

    public static CollectionAppConfig collectionAppConfig;

    private CollectionSingleton(CollectionAppConfig collectionAppConfig) {
        CollectionSingleton.collectionAppConfig = collectionAppConfig;
    }

}
