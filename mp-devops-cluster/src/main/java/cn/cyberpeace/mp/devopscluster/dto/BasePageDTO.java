package cn.cyberpeace.mp.devopscluster.dto;

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

    private Integer page;

    private Integer perPage;


    public BasePageDTO(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
