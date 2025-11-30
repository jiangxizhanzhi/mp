package cn.cyberpeace.mp.devopscluster.vo.xd.one;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueUnit {

    private String value;
    private String unit;
}
