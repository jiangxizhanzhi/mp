package cn.cyberpeace.mp.devops.cluster.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasePageDTO extends BaseDTO{

    /**
     * 每页条数
     */
    private Integer limit;

    /**
     * 页码
     */
    private Integer offset;

    public BasePageDTO(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
