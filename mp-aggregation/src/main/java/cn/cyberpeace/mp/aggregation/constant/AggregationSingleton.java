package cn.cyberpeace.mp.aggregation.constant;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import org.springframework.stereotype.Component;

/**
 * 模块下单例对象的快捷引用
 */
@Component
public class AggregationSingleton {

    /** 模块配置bean的引用, 需要在bean初始化完成之后调用 */
    public static AggregationConfig aggregationConfig;

    private AggregationSingleton(AggregationConfig aggregationConfig) {
        AggregationSingleton.aggregationConfig = aggregationConfig;
    }

}
