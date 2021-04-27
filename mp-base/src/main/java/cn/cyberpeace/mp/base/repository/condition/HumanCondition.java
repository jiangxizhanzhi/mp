package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanCondition {
    private Integer page;
    private Integer pageSize;
    private String search;
    private String[] searchFields;
    private String[] orders;
    private CustomCondition customCondition;

    public Condition toCondition() {
        Condition condition = new Condition();
        condition.setPagination(new PaginationCondition().loadFromPageParams(page, pageSize));
        String[] lineSearchFields = null;
        if (searchFields != null) {
            lineSearchFields = TextUtils.humpToLine(searchFields);
        }
        condition.setSearch(new SearchCondition(search, lineSearchFields));
        condition.setOrder(new OrderCondition(orders));
        condition.setCustom(customCondition);
        return condition;
    }
}
