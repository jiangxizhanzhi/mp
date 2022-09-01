package cn.cyberpeace.mp.base.service.param;

/**
 * 分页兼容查询参数
 */
public interface CompatiblePageQueryParams extends PageQueryParams, CompatibleQueryParams {

    static void ensureCompatible(CompatiblePageQueryParams queryParams) {
        Integer perPage = queryParams.getPerPage();
        if (perPage != null) {
            queryParams.setPageSize(perPage);
        }
    }

    Integer getPerPage();

    void setPerPage(Integer perPage);

    @Override
    default void ensure() {
        ensureCompatible(this);
    }

}
