package cn.cyberpeace.mp.base.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class DiffTargets<T> {

    
    private final Map<String, T> existTargetMap = new HashMap<>();

    
    private final List<T> removingTargets = new ArrayList<>();

    
    private final List<T> addingTargets = new ArrayList<>();

    
    private final List<T> updatingTargets = new ArrayList<>();

    
    private final List<T> keepTargets = new ArrayList<>();

    public static <T> DiffTargets<T> keyDiff(Map<String, T> targetMap, Map<String, T> existTargetMap) {
        return keyDiff(targetMap, existTargetMap, null);
    }

    
    public static <T> DiffTargets<T> keyDiff(Map<String, T> targetMap, Map<String, T> existTargetMap,
                                             JudgeKeepTarget<T> judgeKeepTarget) {
        Map<String, T> existTargetMapCopy = new HashMap<>(existTargetMap);
        DiffTargets<T> diff = new DiffTargets<>();
        diff.getExistTargetMap().putAll(existTargetMap);
        targetMap.forEach((key, target) -> {
            if (existTargetMapCopy.containsKey(key)) {
                var existTarget = existTargetMapCopy.remove(key);
                if (judgeKeepTarget == null) {
                    diff.getUpdatingTargets().add(target);
                } else {
                    if (judgeKeepTarget.judge(target, existTarget)) {
                        diff.getKeepTargets().add(existTarget);
                    } else {
                        diff.getUpdatingTargets().add(target);
                    }
                }
            } else {
                diff.getAddingTargets().add(target);
            }
        });
        diff.getRemovingTargets().addAll(existTargetMapCopy.values());
        return diff;
    }

    @FunctionalInterface
    public interface JudgeKeepTarget<T> {
        boolean judge(T target, T existTarget);
    }
}
