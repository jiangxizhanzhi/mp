package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCond {
    public final String name = "cond";

    private String fieldName;

    private String connector;

    private Object fieldValue;
}
