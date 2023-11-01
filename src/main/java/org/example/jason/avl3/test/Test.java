package org.example.jason.avl3.test;

import org.example.jason.avl3.structure.AVLNode;
import org.example.jason.avl3.structure.AVLTree;
import org.example.jason.avl3.pojo.Gid;

public class Test {
    public static void main(String[] args) {
        GidFactory gidFactory = new GidFactory();
        AVLTree tree = new AVLTree();
        AVLNode node = new AVLNode(gidFactory.produceGid(), 'a');


        for (int i = 0; i < 10; ++i) {
            Gid gid = gidFactory.produceGid();
            System.out.println(gid.getClientId() + "_" + gid.getCounter());
        }

        AVLNode root = new AVLNode(gidFactory.produceGid(), 'a');
        AVLNode node1 = new AVLNode(gidFactory.produceGid(), 'b');
        AVLNode node2 = new AVLNode(gidFactory.produceGid(), 'c');
        AVLNode node3 = new AVLNode(gidFactory.produceGid(), 'd');
        AVLNode node4 = new AVLNode(gidFactory.produceGid(), 'e');
        AVLNode node5 = new AVLNode(gidFactory.produceGid(), 'f');
        AVLNode node6 = new AVLNode(gidFactory.produceGid(), 'g');


    }
}
