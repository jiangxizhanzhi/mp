 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Result.java.
 // File size 472.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo.common;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Result
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServiceResult
 */
@Data
@NoArgsConstructor
public class Result<T> extends ServiceResult<T> {

    public Result(T source) {
        super(source);
        this.asResult();
    }
}
