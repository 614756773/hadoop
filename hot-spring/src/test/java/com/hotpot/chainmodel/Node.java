package com.hotpot.chainmodel;

/**
 * @author qinzhu
 * @since 2020/1/10
 */
public interface Node {
    Object process(NodeChain chain);

    default Object before() {
        return null;
    }

    default Object after() {
        return null;
    }
}