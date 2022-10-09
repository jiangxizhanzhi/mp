package cn.cyberpeace.mp.flow.v2.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowQueryParams extends QueryParams {

    private List<Integer> categories;

    private List<Integer> resourceTypes;

}
