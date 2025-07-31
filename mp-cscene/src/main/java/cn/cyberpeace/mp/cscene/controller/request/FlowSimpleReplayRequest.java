package cn.cyberpeace.mp.cscene.controller.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FlowSimpleReplayRequest {
    @NotBlank
    private String csceneId;

    @NotBlank
    private String pcapId;

    @NotBlank
    private String targetIp;
}
