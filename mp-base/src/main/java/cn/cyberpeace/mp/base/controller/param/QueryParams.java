package cn.cyberpeace.mp.base.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParams {
    private Map<String, Object> params;
    private String search;
    private String[] searchFields;
    private String[] orders;
    private Integer page;
    private Integer pageSize;
    private String[] fields;
}
