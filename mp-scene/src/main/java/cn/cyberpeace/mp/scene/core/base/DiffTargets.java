package cn.cyberpeace.mp.scene.core.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DiffTargets<T> {

    private final List<T> removingTargets = new ArrayList<>();

    private final List<T> addingTargets = new ArrayList<>();

    private final List<T> updatingTargets = new ArrayList<>();

}
