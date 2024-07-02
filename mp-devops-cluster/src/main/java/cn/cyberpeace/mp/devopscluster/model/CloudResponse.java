package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CloudResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CloudResponse<T> {

    private Integer total;
    private List<T> data;
}
