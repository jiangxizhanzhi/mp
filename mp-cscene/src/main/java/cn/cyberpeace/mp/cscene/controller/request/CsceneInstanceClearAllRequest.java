package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

import java.util.List;


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