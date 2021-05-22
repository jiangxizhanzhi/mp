package cn.cyberpeace.mp.base.repository.condition;

import cn.cyberpeace.mp.base.util.ModelUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanCondition {
    private final List<String> selectFields = new ArrayList<>();
    private Integer limit;
    private Integer offset;
    private final List<String> groupFields = new ArrayList<>();
    private final List<String> orders = new ArrayList<>();
    private final AndCondition andCondition = new AndCondition();
    private CustomCondition customCondition;
    private final Map<String, Class<?>> linkedModels = new LinkedHashMap<>();
    private final Map<Class<?>, String> linkedModelNames = new LinkedHashMap<>();
    private String currentLinkName;

    private String genLinkFieldName(String targetName, String fieldName) {
        return StringUtils.joinWith(".", targetName, fieldName);
    }

    private String genLinkFieldName(Class<?> model, String fieldName) {
        String linkName = linkedModelNames.get(model);
        return genLinkFieldName(linkName, fieldName);
    }

    public String currentFieldName(String fieldName) {
        if (currentLinkName == null) {
            return fieldName;
        } else {
            return genLinkFieldName(currentLinkName, fieldName);
        }
    }

    public String currentColumnName(String fieldName) {
        return currentFieldName(TextUtils.humpToLine(fieldName));
    }

    public String[] toSelectFields() {
        return TextUtils.humpToLine(selectFields.toArray(new String[0]));
    }

    public Condition toCondition() {
        var condition = new Condition();
        condition.setPagination(new PaginationCondition(limit, offset));
        condition.setGroup(new GroupCondition(TextUtils.humpToLine(groupFields.toArray(new String[0]))));
        condition.setOrder(new OrderCondition(TextUtils.humpToLine(orders.toArray(new String[0]))));
        condition.setAndi(andCondition);
        condition.setCustom(customCondition);
        return condition;
    }

    public String toTableName() {
        if (linkedModels.isEmpty()) {
            return null;
        }
        List<String> tableNames = new ArrayList<>();
        linkedModels.forEach((linkName, model) -> tableNames.add(
                StringUtils.joinWith(" ", ModelUtils.getTableName(model), linkName)));
        return StringUtils.joinWith(", ", tableNames.toArray());
    }

    public HumanCondition linkAs(Class<?> model, String linkName) {
        linkedModels.put(linkName, model);
        linkedModelNames.put(model, linkName);
        currentLinkName = linkName;
        return this;
    }

    public HumanCondition link(Class<?>... models) {
        for (Class<?> model: models) {
            String linkName = "m" + linkedModels.size();
            linkAs(model, linkName);
        }
        return this;
    }

    public HumanCondition currentLink(String linkName) {
        currentLinkName = linkName;
        return this;
    }

    public HumanCondition currentLink(Class<?> model) {
        String linkName = linkedModelNames.get(model);
        return currentLink(linkName);
    }

    public HumanCondition selectAs(String fieldName, String outputFieldName) {
        selectFields.add(StringUtils.joinWith(" ", currentFieldName(fieldName), outputFieldName));
        return this;
    }

    public HumanCondition select(String... fieldNames) {
        for (String fieldName: fieldNames) {
            selectFields.add(currentFieldName(fieldName));
        }
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

    public HumanCondition groupBy(String... fieldNames) {
        for (String fieldName: fieldNames) {
            groupFields.add(currentFieldName(fieldName));
        }
        return this;
    }

    public HumanCondition orderBy(String... orders) {
        for (String order: orders) {
            String linkedOrder;
            if (order.startsWith("-")) {
                linkedOrder = "-" + currentFieldName(order.substring(1));
            } else {
                linkedOrder = currentFieldName(order);
            }
            this.orders.add(linkedOrder);
        }
        return this;
    }

    public HumanCondition filterLink(String fieldName, Class<?> targetModel, String targetFieldName) {
        andCondition.addLink(currentColumnName(fieldName),
                genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    public HumanCondition filterLink(Class<?> sourceModel, String sourceFieldName,
                                     Class<?> targetModel, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceModel, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetModel, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    public HumanCondition filterLink(String sourceLinkName, String sourceFieldName,
                                     String targetLinkName, String targetFieldName) {
        andCondition.addLink(genLinkFieldName(sourceLinkName, TextUtils.humpToLine(sourceFieldName)),
                genLinkFieldName(targetLinkName, TextUtils.humpToLine(targetFieldName)));
        return this;
    }

    public HumanCondition filterIn(String fieldName, Object[] fieldValue) {
        andCondition.addIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotIn(String fieldName, Object[] fieldValue) {
        andCondition.addNotIn(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterEqual(String fieldName, Object fieldValue) {
        andCondition.addEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotEqual(String fieldName, Object fieldValue) {
        andCondition.addNotEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterGreaterThan(String fieldName, Object fieldValue) {
        andCondition.addGreaterThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterGreaterThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addGreaterThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterLessThan(String fieldName, Object fieldValue) {
        andCondition.addLessThan(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterLessThanOrEqual(String fieldName, Object fieldValue) {
        andCondition.addLessThanOrEqual(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterContains(String fieldName, Object fieldValue) {
        andCondition.addContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterIContains(String fieldName, Object fieldValue) {
        andCondition.addIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterStartsWith(String fieldName, Object fieldValue) {
        andCondition.addStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterEndsWith(String fieldName, Object fieldValue) {
        andCondition.addEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotContains(String fieldName, Object fieldValue) {
        andCondition.addNotContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotIContains(String fieldName, Object fieldValue) {
        andCondition.addNotIContains(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotIStartsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIStartsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    public HumanCondition filterNotIEndsWith(String fieldName, Object fieldValue) {
        andCondition.addNotIEndsWith(currentColumnName(fieldName), fieldValue);
        return this;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrConditionPoxy {

        private HumanCondition humanCondition;

        private final OrCondition orCondition = new OrCondition();

        public OrConditionPoxy orIn(String fieldName, Object[] fieldValue) {
            orCondition.addIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotIn(String fieldName, Object[] fieldValue) {
            orCondition.addNotIn(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orEqual(String fieldName, Object fieldValue) {
            orCondition.addEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotEqual(String fieldName, Object fieldValue) {
            orCondition.addNotEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orGreaterThan(String fieldName, Object fieldValue) {
            orCondition.addGreaterThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orGreaterThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addGreaterThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orLessThan(String fieldName, Object fieldValue) {
            orCondition.addLessThan(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orLessThanOrEqual(String fieldName, Object fieldValue) {
            orCondition.addLessThanOrEqual(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orContains(String fieldName, Object fieldValue) {
            orCondition.addContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orIContains(String fieldName, Object fieldValue) {
            orCondition.addIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orStartsWith(String fieldName, Object fieldValue) {
            orCondition.addStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orEndsWith(String fieldName, Object fieldValue) {
            orCondition.addEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotContains(String fieldName, Object fieldValue) {
            orCondition.addNotContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotIContains(String fieldName, Object fieldValue) {
            orCondition.addNotIContains(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotIStartsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIStartsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
            return this;
        }

        public OrConditionPoxy orNotIEndsWith(String fieldName, Object fieldValue) {
            orCondition.addNotIEndsWith(humanCondition.currentColumnName(fieldName), fieldValue);
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
