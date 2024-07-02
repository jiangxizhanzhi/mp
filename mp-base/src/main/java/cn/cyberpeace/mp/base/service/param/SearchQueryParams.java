package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName SearchQueryParams
 * @Description 搜索查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
