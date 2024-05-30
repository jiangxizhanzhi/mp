package cn.cyberpeace.mp.cscene.model;

import lombok.Data;

@Data
public class CsceneElementStat {
    private String csceneId;
    private Integer elementType;
    private Integer elementTypeCount;
    private Integer cpu;
    private Integer disk;
    private Integer ram;
}
