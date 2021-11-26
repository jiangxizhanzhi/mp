package cn.cyberpeace.mp.base.service.param;

/**
 * 排序查询参数
 */
public interface CompatibleOrderQueryParams {

    static void ensureCompatible(CompatibleOrderQueryParams queryParams) {
        String[] sort = queryParams.getSort();
        if (sort != null && sort.length > 0) {
            queryParams.setOrders(sort);
        }
    }

    void setOrders(String[] orders);

    String[] getSort();

    default void ensure() {
        ensureCompatible(this);
    }
}
