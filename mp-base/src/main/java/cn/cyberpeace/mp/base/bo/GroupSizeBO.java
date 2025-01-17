 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GroupSizeBO.java.
 // File size 163.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupSizeBO<T, S extends Number> {
    private T target;
    private S size;
}
