package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName CollectionSingleton
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class CollectionSingleton {

    public static CollectionAppConfig collectionAppConfig;

    /**
     * CollectionSingleton
     *
     * @param collectionAppConfig
     */
    private CollectionSingleton(CollectionAppConfig collectionAppConfig) {
        CollectionSingleton.collectionAppConfig = collectionAppConfig;
    }

}
