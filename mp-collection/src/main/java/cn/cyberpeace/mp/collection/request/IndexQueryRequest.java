package cn.cyberpeace.mp.collection.request;

import lombok.Data;

@Data
public class IndexQueryRequest {
    private String index;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
