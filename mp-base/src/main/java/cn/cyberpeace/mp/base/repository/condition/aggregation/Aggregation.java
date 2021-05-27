package cn.cyberpeace.mp.base.repository.condition.aggregation;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public abstract class Aggregation {

    private String fieldName;

    public abstract String getFuncName();

    public String toSqlField(String columnName) {
        return StringUtils.join(getFuncName(), "(", columnName, ")");
    }

}
