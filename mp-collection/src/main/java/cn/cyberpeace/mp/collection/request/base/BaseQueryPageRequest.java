package cn.cyberpeace.mp.collection.request.base;

import lombok.Data;

@Data
public class BaseQueryPageRequest {
    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
