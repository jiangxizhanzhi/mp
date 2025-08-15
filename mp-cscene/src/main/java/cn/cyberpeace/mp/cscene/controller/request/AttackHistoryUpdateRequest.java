package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

@Data
public class AttackHistoryUpdateRequest {
    private String id;
    private Boolean attackEffectedFlag;
}
