package cn.cyberpeace.mp.base.service.param;

/**
 * 搜索查询参数
 */
public interface SearchQueryParams {

    String getSearch();

    String[] getSearchFields();

    void setSearch(String search);

    void setSearchFields(String[] searchFields);

}
