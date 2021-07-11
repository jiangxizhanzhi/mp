package cn.cyberpeace.mp.base.service.param;


public interface CompatibleOrderQueryParams extends OrderQueryParams, CompatibleQueryParams {

    static void ensureCompatible(CompatibleOrderQueryParams queryParams) {
        String[] sort = queryParams.getSort();
        if (sort != null && sort.length > 0) {
            queryParams.setOrders(sort);
        }
    }

    String[] getSort();

    void setSort(String[] sort);

    default void ensure() {
        ensureCompatible(this);
    }
}
