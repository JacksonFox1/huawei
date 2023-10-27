package org.example.jason.avl2.structure;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.example.jason.avl2.config.ConstantData;

public class AVLNode<V> {
    //key为gid
    Long gid;
    V values;
    //该节点所在的高度
    int height;
    //树的大小，一是起到pos的作用，二和key的作用类似，都是起到加速查询的作用
    int size;
    //是否删除的标记，为true则为被删除，为false则未被删除
    boolean tombstone;
    //全局唯一表示
    AVLNode<V> left;

    AVLNode<V> right;

    AVLNode<V> parent;

    AVLNode(V value) {
        this.gid = Long.parseLong(ConstantData.MACHINE_ID + (ConstantData.counter++).toString());
        this.values = value;
        this.height = 1;
        this.size = 1;
        this.tombstone = false;
        this.left = null;
        this.right = null;
        this.parent = null;
    }

}
