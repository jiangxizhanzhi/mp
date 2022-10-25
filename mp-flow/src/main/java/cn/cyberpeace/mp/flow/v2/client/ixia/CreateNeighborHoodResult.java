package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Data
public class CreateNeighborHoodResult extends IxiaResponse {
    private Long createdOn;
    private String author;
    private String createdBy;
    private List<Object> networkModel;
    private String name;
    private String type;
}
