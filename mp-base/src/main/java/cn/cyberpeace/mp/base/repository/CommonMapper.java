package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.repository.condition.Condition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CommonMapper {
    /**
     * list
     *
     * @param table
     * @param condition
     * @param selectFields
     * @return
     */
    List<Map<String, Object>> list(String table, @Param("condition") Condition condition, String[] selectFields);

    /**
     * total
     *
     * @param table
     * @param condition
     * @return int
     */
    int total(String table, @Param("condition") Condition condition);

    /**
     * exist
     *
     * @param table
     * @param condition
     * @return boolean
     */
    boolean exist(String table, @Param("condition") Condition condition);

    /**
     * get
     *
     * @param table
     * @param id
     * @param selectFields
     * @return
     */
    Map<String, Object> get(String table, String id, String[] selectFields);

    /**
     * create
     *
     * @param table
     * @param createFields
     * @param createFieldValues
     * @return boolean
     */
    boolean create(String table, String[] createFields, Object[] createFieldValues);

    /**
     * update
     *
     * @param table
     * @param id
     * @param updateFields
     * @param updateFieldValues
     * @return boolean
     */
    boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);

    /**
     * delete
     *
     * @param table
     * @param id
     * @return boolean
     */
    boolean delete(String table, String id);

    /**
     * bulkCreate
     *
     * @param table
     * @param createFields
     * @param createFieldValuesList
     * @return int
     */
    int bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);

    /**
     * bulkUpdate
     *
     * @param table
     * @param updateFields
     * @param updateFieldValuesList
     * @return int
     */
    int bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);

    /**
     * bulkDelete
     *
     * @param table
     * @param ids
     * @return int
     */
    int bulkDelete(String table, String[] ids);

    /**
     * filterUpdate
     *
     * @param table
     * @param updateFields
     * @param updateFieldValues
     * @param condition
     * @return int
     */
    int filterUpdate(String table, String[] updateFields, Object[] updateFieldValues,
                     @Param("condition") Condition condition);

    /**
     * filterDelete
     *
     * @param table
     * @param condition
     * @return int
     */
    int filterDelete(String table, @Param("condition") Condition condition);
}
