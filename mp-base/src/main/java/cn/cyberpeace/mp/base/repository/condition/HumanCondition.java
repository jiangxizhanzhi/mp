package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanCondition {
    private Integer limit;
    private Integer offset;
    private String search;
    private final List<String> searchFields = new ArrayList<>();
    private final List<String> groupFields = new ArrayList<>();
    private final List<String> orders = new ArrayList<>();
    private final AndCondition andCondition = new AndCondition();
    private CustomCondition customCondition;

    public Condition toCondition() {
        var condition = new Condition();
        condition.setPagination(new PaginationCondition(limit, offset));
        condition.setSearch(new SearchCondition(search, TextUtils.humpToLine(searchFields.toArray(new String[0]))));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields.toArray(new String[0]))));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders.toArray(new String[0]))));
        condition.setAndi(andCondition);
        condition.setCustom(customCondition);
        return condition;
    }

    public HumanCondition search(String search) {
        this.search = search;
        return this;
    }

    public HumanCondition addSearchField(String fieldName) {
        searchFields.add(fieldName);
        return this;
    }

    public HumanCondition addSearchFields(String[] fieldNames) {
        searchFields.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public HumanCondition pagination(int page, int pageSize) {
        if (pageSize >= 0) {
            limit = pageSize;
        } else {
            limit = null;
            offset = null;
            return this;
        }

        if (page > 0) {
            offset = (page - 1) * limit;
        } else {
            offset = null;
        }

        return this;
    }

    public HumanCondition slice(int startIndex, int endIndex) {
        offset = Math.max(startIndex, 0);
        limit = Math.max(endIndex - offset, 0);
        return this;
    }

    public HumanCondition groupBy(String fieldName) {
        groupFields.add(fieldName);
        return this;
    }

    public HumanCondition groupBy(String[] fieldNames) {
        groupFields.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public HumanCondition orderBy(String order) {
        orders.add(order);
        return this;
    }

    public HumanCondition orderBy(String[] orders) {
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    public HumanCondition filterIn(String fieldName, Object[] fieldValue) {
        andCondition.addIn(TextUtils.humpToLine(fieldName), fieldValue);
        return this;
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

        public OrConditionPoxy orIn(String fieldName, Object[] fieldValue) {
            orCondition.addIn(TextUtils.humpToLine(fieldName), fieldValue);
            return this;
        }

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
