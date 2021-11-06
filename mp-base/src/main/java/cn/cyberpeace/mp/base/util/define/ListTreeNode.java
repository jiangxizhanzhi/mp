package cn.cyberpeace.mp.base.util.define;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTreeNode<T> {

    private final T value;

    private final List<ListTreeNode<T>> children = new ArrayList<>();

    public ListTreeNode() {
        value = null;
    }

    public ListTreeNode(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @SafeVarargs
    public final ListTreeNode<T> addChild(ListTreeNode<T>... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public List<ListTreeNode<T>> getChildren() {
        return children;
    }
}
