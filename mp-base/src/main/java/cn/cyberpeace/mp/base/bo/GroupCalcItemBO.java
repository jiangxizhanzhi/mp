 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GroupCalcItemBO.java.
 // File size 162.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupCalcItemBO<T1, T2> {
    private T1 target;
    private T2 calcSource;
}
