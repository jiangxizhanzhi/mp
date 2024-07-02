package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ConnectorCond
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorCond {
    @SuppressWarnings("java:S1170")
    public final String name = "cond";

    private String fieldName;

    private String connector;

    private Object fieldValue;
}
