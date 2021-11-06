package cn.cyberpeace.mp.base.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差异比较结果
 * @param <T> 目标类型
 */
@Data
@NoArgsConstructor
public class DiffTargets<T> {

    /**
     * 存在的目标集合
     */
    private final Map<String, T> existTargetMap = new HashMap<>();

    /**
     * 待移除的目标集合
     */
    private final List<T> removingTargets = new ArrayList<>();

    /**
     * 待添加的目标集合
     */
    private final List<T> addingTargets = new ArrayList<>();

    /**
     * 待更新的目标集合
     */
    private final List<T> updatingTargets = new ArrayList<>();

    /**
     * 保持不变的目标集合
     */
    private final List<T> keepTargets = new ArrayList<>();

    public static <T> DiffTargets<T> keyDiff(Map<String, T> targetMap, Map<String, T> existTargetMap) {
        return keyDiff(targetMap, existTargetMap, null);
    }

    /**
     * 对目标集合和存在的目标集合进行关键字差异比较
     * @param targetMap 目标集合
     * @param existTargetMap 存在的目标集合
     * @param judgeKeepTarget 判断目标是否保持无需更新
     * @param <T> 目标类型
     * @return 差异比较结果
     */
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
