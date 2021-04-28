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
    private String[] groupFields;
    private String[] orders;
    private CustomCondition customCondition;

    public Condition toCondition() {
        Condition condition = new Condition();
        condition.setPagination(new PaginationCondition().loadFromPageParams(page, pageSize));
        condition.setSearch(new SearchCondition(search, TextUtils.humpToLine(searchFields)));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields)));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders)));
        condition.setCustom(customCondition);
        return condition;
    }
}
