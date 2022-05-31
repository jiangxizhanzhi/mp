package cn.cyberpeace.mp.devops.cluster.response.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResult<T> {

    private List<T> list;

}
