 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ListTreeNode.java.
 // File size 718.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
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
