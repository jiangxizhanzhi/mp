 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IntervalSizeBO.java.
 // File size 364.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntervalSizeBO<T, S extends Number> {
    private T start;
    private T end;
    private S size;

    public IntervalSizeBO(T start, T end, S size) {
        this.start = start;
        this.end = end;
        this.size = size;
    }
}
