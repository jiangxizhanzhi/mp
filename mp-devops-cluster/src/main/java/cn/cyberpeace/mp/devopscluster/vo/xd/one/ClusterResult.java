package cn.cyberpeace.mp.devopscluster.vo.xd.one;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterResult {

    @JsonProperty("uuid")
    private String uuid;

}
