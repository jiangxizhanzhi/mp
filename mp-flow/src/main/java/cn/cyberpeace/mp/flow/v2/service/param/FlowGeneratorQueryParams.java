package cn.cyberpeace.mp.flow.v2.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowGeneratorQueryParams extends QueryParams {

    private List<String> types;

}
