package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName FieldsQueryParams
 * @Description 排序查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface FieldsQueryParams {

    /**
     * getFields
     *
     * @return
     */
    String[] getFields();

    /**
     * setFields
     *
     * @param fields
     */
    void setFields(String[] fields);

}
