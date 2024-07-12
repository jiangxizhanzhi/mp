 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AggregationSingleton.java.
 // File size 769.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.constant;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import org.springframework.stereotype.Component;

/**
 * 模块下单例对象的快捷引用
 */
/**
 * @ClassName AggregationSingleton
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class AggregationSingleton {

    /** 模块配置bean的引用, 需要在bean初始化完成之后调用 */
    public static AggregationConfig aggregationConfig;

    /**
     * AggregationSingleton
     *
     * @param aggregationConfig
     */
    private AggregationSingleton(AggregationConfig aggregationConfig) {
        AggregationSingleton.aggregationConfig = aggregationConfig;
    }

}
