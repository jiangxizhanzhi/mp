package cn.cyberpeace.mp.cscene.model;

import lombok.Data;

/**
 * @ClassName CsceneElementStat
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneElementStat {
    private String csceneId;
    private Integer elementType;
    private Integer elementTypeCount;
    private Integer cpu;
    private Integer disk;
    private Integer ram;
}
