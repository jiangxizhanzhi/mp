package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.annotation.Recommendation;
import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;

import java.util.List;

public interface BasePermissionMapper<M extends BasePermissionModel> extends BaseModelMapper {


    @Recommendation
    default List<M> listModelsByOwner(String owner) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return list(humanCondition);
    }


    @Recommendation
    default List<M> listModelsByTarget(String target) {

        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return list(humanCondition);
    }

    @Recommendation
    default int deleteByOwner(String owner) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("owner", owner);

        return filterDelete(humanCondition);
    }


    @Recommendation
    default int deleteByTarget(String target) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual("target", target);

        return filterDelete(humanCondition);
    }


}
