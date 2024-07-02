package cn.cyberpeace.mp.base.service.param;

/**
 * 搜索查询参数
 */
public interface SearchQueryParams {

    /**
     * getSearch
     *
     * @return
     */
    String getSearch();

    /**
     * getSearchFields
     *
     * @return
     */
    String[] getSearchFields();

    /**
     * setSearch
     *
     * @param search
     */
    void setSearch(String search);

    /**
     * setSearchFields
     *
     * @param searchFields
     */
    void setSearchFields(String[] searchFields);

}
