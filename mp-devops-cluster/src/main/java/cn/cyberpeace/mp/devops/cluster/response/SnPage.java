package cn.cyberpeace.mp.devops.cluster.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnPage {

    /**
     * 数据总条数
     */
    private Long total = NumberUtils.LONG_ZERO;

    /**
     * 当前页码
     */
    private Long current_page;

    /**
     * 从第几条数据开始
     */
    private Long from;

    /**
     * 到第几条数据结束
     */
    private Long to;

    /**
     * 最后一页
     */
    private Long last_page;

    /**
     * 上一页地址
     */
    private String perv_page_url;

    /**
     * 下一页地址
     */
    private String next_page_url;
}
