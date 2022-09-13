package cn.cyberpeace.mp.sceneplugingns3.util.gns3http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudListData<T> {
    private List<T> list;
    private Integer total;
}
