package cn.cyberpeace.mp.collection.model.log;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageableLogs {
    // 结果总的日志数目
    private Long total;
    // 当前查询返回的日志数目
    private Long size;
    // 总的页数
    private Long totalPage;
    // 当前第几页
    private Long currentPage;
    // 每页日志数目
    private Long pageSize;
    // 数据
    private List<DeviceLog> data;
}
