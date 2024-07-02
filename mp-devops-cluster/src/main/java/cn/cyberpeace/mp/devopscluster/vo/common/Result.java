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
