package cn.cyberpeace.mp.devops.cluster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePageDTO extends BaseDTO{

    /**
     * 每页条数
     */
    private Integer limit;

    /**
     * 偏移量
     */
    private Integer offset;
}
