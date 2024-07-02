package cn.cyberpeace.mp.base.util.define;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ListTreeNode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ListTreeNode<T> {

    private final T value;

    private final List<ListTreeNode<T>> children = new ArrayList<>();

    /**
     * ListTreeNode
     */
    public ListTreeNode() {
        value = null;
    }

    /**
     * ListTreeNode
     *
     * @param value
     */
    public ListTreeNode(T value) {
        this.value = value;
    }

    /**
     * get
     *
     * @return
     */
    public T get() {
        return value;
    }

    /**
     * addChild
     *
     * @param children
     * @return
     */
    @SafeVarargs
    public final ListTreeNode<T> addChild(ListTreeNode<T>... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    /**
     * getChildren
     *
     * @return
     */
    public List<ListTreeNode<T>> getChildren() {
        return children;
    }
}
