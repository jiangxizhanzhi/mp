 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GroupCalcItemBO.java.
 // File size 279.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.bo;

import lombok.Data;

/**
 * @ClassName GroupCalcItemBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class GroupCalcItemBO<T1, T2> {
    private T1 target;
    private T2 calcSource;
}
