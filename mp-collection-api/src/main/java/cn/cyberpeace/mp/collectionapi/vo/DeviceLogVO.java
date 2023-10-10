package cn.cyberpeace.mp.collectionapi.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeviceLogVO {
    private Long total;
    private Long size;
    private Long totalPage;
    private Long currentPage;
    private Long pageSize;
    private List<Object> data;
}
