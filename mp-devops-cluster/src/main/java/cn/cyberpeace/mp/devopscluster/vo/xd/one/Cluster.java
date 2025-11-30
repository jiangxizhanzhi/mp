package cn.cyberpeace.mp.devopscluster.vo.xd.one;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cluster {

    @JsonProperty("count")
    private Integer count;
    @JsonProperty("results")
    private List<ClusterResult> results;

}
