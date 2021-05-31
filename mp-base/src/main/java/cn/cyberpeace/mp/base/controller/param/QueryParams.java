package cn.cyberpeace.mp.base.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 用于controller层的查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParams {
    /** 具体业务参数 */
    private Map<String, Object> params;
    /** 搜索关键词 */
    private String search;
    /** 搜索字段 */
    private String[] searchFields;
    /** 排序字段 */
    private String[] orders;
    /** 分页页码 */
    private Integer page;
    /** 分页每页条数 */
    private Integer pageSize;
    /** 指定的返回字段 */
    private String[] fields;
}
