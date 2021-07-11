package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseBO {

    
    private Long attackNumber;

    
    private Double trafficSize;

    
    private BigDecimal attackNumberPercent;

    
    private BigDecimal attackTrafficPercent;

}
