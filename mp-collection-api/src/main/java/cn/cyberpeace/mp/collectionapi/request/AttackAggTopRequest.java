package cn.cyberpeace.mp.collectionapi.request;

import lombok.Data;

@Data
public class AttackAggTopRequest extends AttackFlowBaseRequest {
    private Integer topSize = 3;
}
