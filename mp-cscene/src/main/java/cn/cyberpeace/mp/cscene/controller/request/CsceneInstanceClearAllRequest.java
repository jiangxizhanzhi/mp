 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceClearAllRequest.java.
 // File size 781.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneInstanceClearAllRequest
 * @Description
 * @Author zhanglin
 * @Date 2023/3/21
 * @Version 1.0.0
 */
@Data
public class CsceneInstanceClearAllRequest {
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