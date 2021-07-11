package cn.cyberpeace.mp.base.service.param;


public interface SearchQueryParams {

    String getSearch();

    String[] getSearchFields();

    void setSearch(String search);

    void setSearchFields(String[] searchFields);

}
