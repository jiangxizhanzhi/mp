 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IntervalSizeBO.java.
 // File size 480.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName IntervalSizeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
