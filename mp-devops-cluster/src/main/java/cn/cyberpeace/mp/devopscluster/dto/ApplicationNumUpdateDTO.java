package cn.cyberpeace.mp.devopscluster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNumUpdateDTO {
    private Integer operate;
    private String uuid;
}