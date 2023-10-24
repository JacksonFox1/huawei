package org.example.jason.avl2.structure;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.example.jason.avl2.config.ConstantData;

public class AVLNode<V> {
    //key为gid
    Long key;
    V values;
    //该节点所在的高度
    int height;
    //树的大小
    int size;
    //是否删除的标记，为true则为被删除，为false则未被删除
    boolean tombstone;
    //全局唯一表示
    AVLNode<V> left;

    AVLNode<V> right;

    AVLNode(V value) {
        this.key = Long.parseLong(ConstantData.MACHINE_ID + (ConstantData.counter++).toString());
        this.values = value;
        this.height = 1;
        this.size = 1;
        this.tombstone = false;
        this.left = null;
        this.right = null;
    }


//    private Integer hash(K key) {
//        return Objects.hashCode(key);
//    }
   
}
