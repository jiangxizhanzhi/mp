package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "原始IP数据")
public class OriginIpsResponse {
    private Controller controller;
    private List<Compute> compute;
    private Office office;

    @Data
    private static class Controller {
        private String ip;
        private String netmask;
        private String gateway;
        private String allocationPools;
    }

    @Data
    private static class Compute {
        private String name;
        private String ip;
    }

    @Data
    private static class Office {
        private String ip;
    }
}