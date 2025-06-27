package cn.cyberpeace.mp.devopscluster.request;

import lombok.Data;

@Data
public class ImageCreateRequest {
    private Integer  vlanId;
    private String cidr;
}
