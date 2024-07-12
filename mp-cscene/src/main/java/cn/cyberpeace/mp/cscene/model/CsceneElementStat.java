 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneElementStat.java.
 // File size 410.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
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
