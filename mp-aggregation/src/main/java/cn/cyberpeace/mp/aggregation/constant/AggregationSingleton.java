package cn.cyberpeace.mp.aggregation.constant;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import org.springframework.stereotype.Component;


@Component
public class AggregationSingleton {

    
    public static AggregationConfig aggregationConfig;

    private AggregationSingleton(AggregationConfig aggregationConfig) {
        AggregationSingleton.aggregationConfig = aggregationConfig;
    }

}
