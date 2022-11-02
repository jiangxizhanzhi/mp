package cn.cyberpeace.mp.flow.v2.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_task")
public class FlowTaskModel extends BaseModel {
    private String name;
    @DefaultStr
    private String description;
    /**
     * 流量任务分类：0 移动互联网，1 典型互联网，2 城市供水控制网，3 城市管网，4 电力调度网，5 轨道交通运行控制网，6 物联网，7 广播电视网，8 金融网等行业网络
     */
    @DefaultInt
    private Integer category;
    @DefaultInt
    private Integer interactiveType;
    @DefaultInt
    private Integer bandwidth;
    /**
     * 负载模式：随机负载 random，二项分布 binomial， 均匀分布 uniformity， 指数分布 index， 高斯分布 gauss
     */
    @DefaultStr(LoadModeType.RANDOM)
    private String loadMode;
    @DefaultStr
    private String filterProtocols;
    @DefaultStr
    private String flows;

    public static class LoadModeType {
        public static final String RANDOM = "random";
        public static final String BINOMIAL = "binomial";
        public static final String UNIFORMITY = "uniformity";
        public static final String INDEX = "index";
        public static final String GAUSS = "gauss";

        private LoadModeType() {}
    }
}
