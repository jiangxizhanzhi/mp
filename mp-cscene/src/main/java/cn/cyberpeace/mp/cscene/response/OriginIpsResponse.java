 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OriginIpsResponse.java.
 // File size 935.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName OriginIpsResponse
 * @Description
 * @Author zhanglin
 * @Date 2023/3/21
 * @Version 1.0.0
 */
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