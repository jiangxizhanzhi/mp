package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCondition implements BaseCondition {

    private String search;

    private String[] searchFields;

    public String getSearchPattern() {
        return "%" + search + "%";
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isEmpty(search) || (searchFields == null || searchFields.length == 0);
    }
}
