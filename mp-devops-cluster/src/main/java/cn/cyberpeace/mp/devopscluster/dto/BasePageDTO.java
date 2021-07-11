package cn.cyberpeace.mp.devopscluster.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasePageDTO extends BaseDTO{

    
    private Integer limit;

    
    private Integer offset;

    public BasePageDTO(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
