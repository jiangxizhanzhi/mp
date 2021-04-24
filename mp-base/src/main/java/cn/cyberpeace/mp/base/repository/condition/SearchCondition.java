package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCondition extends BaseCondition {

    private String search;

    private String[] searchFields;

    public String getSearchPattern() {
        return "%" + search + "%";
    }

    @Override
    public Boolean isEmpty() {
        return StringUtils.isEmpty(search) || (searchFields == null || searchFields.length == 0);
    }
}
