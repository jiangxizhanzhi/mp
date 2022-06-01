package cn.cyberpeace.mp.devops.cluster.vo.common;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> extends ServiceResult<T> {

    public Result(T source) {
        super(source);
        this.asResult();
    }
}
