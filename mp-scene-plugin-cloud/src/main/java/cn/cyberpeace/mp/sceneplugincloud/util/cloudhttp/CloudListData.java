package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName CloudListData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudListData<T> {
    private List<T> list;
    private Integer total;
}
