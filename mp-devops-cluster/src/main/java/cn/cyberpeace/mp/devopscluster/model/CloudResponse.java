package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;

import java.util.List;

@Data
public class CloudResponse<T> {

    private Integer total;
    private List<T> data;
}
