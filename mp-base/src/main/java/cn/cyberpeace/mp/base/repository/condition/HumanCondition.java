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
    private final AndCondition andCondition = new AndCondition();
    private CustomCondition customCondition;

    public Condition toCondition() {
        var condition = new Condition();
        condition.setPagination(new PaginationCondition().loadFromPageParams(page == null ? 0 : page,
                pageSize == null ? 0 : pageSize));
        condition.setSearch(new SearchCondition(search, TextUtils.humpToLine(searchFields)));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields)));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders)));
        condition.setAndi(andCondition);
        condition.setCustom(customCondition);
        return condition;
    }

    public HumanCondition filterEqual(String fieldName, Object fieldValue) {
        andCondition.addEqual(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotEqual(String fieldName, Object fieldValue) {
        andCondition.addNotEqual(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterGreaterThan(String fieldName, Object fieldValue) {
        andCondition.addGreaterThan(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterGreaterThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addGreaterThanOrEqual(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterLessThan(String fieldName, Object fieldValue) {
        andCondition.addLessThan(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterLessThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addLessThanOrEqual(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrConditionPoxy {

        private HumanCondition humanCondition;

        private final OrCondition orCondition = new OrCondition();

        public OrConditionPoxy orEqual(String fieldName, Object fieldValue) {
            orCondition.addEqual(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotEqual(String fieldName, Object fieldValue) {
            orCondition.addNotEqual(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orGreaterThan(String fieldName, Object fieldValue) {
            orCondition.addGreaterThan(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orGreaterThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addGreaterThanOrEqual(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orLessThan(String fieldName, Object fieldValue) {
            orCondition.addLessThan(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orLessThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addLessThanOrEqual(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

        public HumanCondition filterOrEnd() {
            humanCondition.getAndCondition().addCondition(orCondition);
            return humanCondition;
        }
    }

    public OrConditionPoxy filterOrStart() {
        return new OrConditionPoxy(this);
    }

}
