/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.jason.avl2.structure;

import java.util.HashMap;
import java.util.Map;

public class AVLTree<V> {
    Map<Long, AVLNode> cache;
    AVLNode<V> root;

    public AVLTree() {
        root = null;
        cache = new HashMap<>();
    }

    public AVLTree(AVLNode<V> root) {
        this.root = root;
        cache = new HashMap<>();
    }

    int getHeight(AVLNode<V> N) {
        if (N == null)
            return 0;
        return N.height;
    }

    int getSize(AVLNode<V> N) {
        if (N == null) {
            return 0;
        }
        return N.size;
    }

    int max(int a, int b) {
        return Math.max(a, b);
    }

    /**
     * 向右旋，对应LL不平衡
     * @param T
     * @return
     */
    public AVLNode<V> rightRotation(AVLNode<V> T) {
        AVLNode<V> T1 = T.left;
        AVLNode<V> R1 = T1.right;

        // Perform rotation
        T1.right = T;
        T.left = R1;

        // Update heights
        T.height = max(getHeight(T.left), getHeight(T.right)) + 1;
        T1.height = max(getHeight(T1.left), getHeight(T1.right)) + 1;

        // Return new root
        return T1;
    }

    /**
     * 向左旋，对应RR不平衡
     * @param T
     * @return
     */
    public AVLNode<V> leftRotation(AVLNode<V> T) {
        AVLNode<V> T1 = T.right;
        AVLNode<V> L1 = T1.left;

        // Perform rotation
        T1.left = T;
        T.right = L1;

        // Update heights
        T.height = max(getHeight(T.left), getHeight(T.right)) + 1;
        T1.height = max(getHeight(T1.left), getHeight(T1.right)) + 1;

        // Return new root
        return T1;
    }

    // Get Balance factor of node N
    int getBalance(AVLNode<V> N) {
        if (N == null)
            return 0;

//        return height(N.left) - height(N.right);
        return getHeight(N.right) - getHeight(N.left);
    }

    private AVLNode<V> insert(AVLNode<V> node, Long key, V value) {

        /* 1.  Perform the normal BST insertion */
        if (node == null)
            return (new AVLNode<V>(key, value));

        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key, value);
        else // Duplicate keys not allowed
            return node;
        /* 2. Update height of this ancestor node */
        return rebalance(node);
    }


    public void insert(Long key, V value) {
        this.root = insert(this.root, key, value);
    }

    public void delete(Long key) {
        this.root = this.delete(this.root, key);
    }

    private AVLNode<V> delete(AVLNode<V> root, Long key) {
        if (root == null) {
            return null;
        }
        if (key.compareTo(root.key) < 0) {
            root.left = delete(root.left, key);
        } else if (key.compareTo(root.key) > 0) {
            root.right = delete(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.key = findSuccessor(root).key;
            root.right = delete(root.right, root.key);
        }
        if (root != null) {
            root = rebalance(root);
        }
        return root;
    }

    public Long search(Long key) {
        if (search(root, key) == null)
            return null;
        else
            return (search(root, key).key);
    }

    public V getValue(Long key) {
        if (search(root, key) == null)
            return null;
        else
            return (search(root, key).values);

    }

    private AVLNode<V> search(AVLNode<V> root, Long key) {
        if (root == null) {
            return null;
        } else if (key.compareTo(root.key) == 0) {
            return root;
        } else if (key.compareTo(root.key) > 0) {
            return search(root.right, key);
        } else {
            return search(root.left, key);
        }
    }

    private AVLNode<V> getNodeByPos(AVLNode<V> node, int pos) {
        if (node == null) {
            return null;
        }
        int leftSize = getSize(node.left);
        if (leftSize + 1 == pos) {
            return node;
        } else if (leftSize >= pos) {
            return getNodeByPos(node.left, pos);
        } else {
            return getNodeByPos(node.right, pos - (leftSize + 1));
        }
    }

    public AVLNode<V> getNodeByPos(int pos) {
        return getNodeByPos(root, pos);
    }


    private AVLNode<V> rebalance(AVLNode<V> z) {
        z.height = 1 + max(getHeight(z.left), getHeight(z.right));
        int balance = getBalance(z);
        if (balance > 1) {
            if (getHeight(z.right.right) > getHeight(z.right.left)) {
                z = leftRotation(z);
            } else {
                z.right = rightRotation(z.right);
                z = leftRotation(z);
            }
        } else if (balance < -1) {
            if (getHeight(z.left.left) > getHeight(z.left.right)) {
                z = rightRotation(z);
            } else {
                z.left = leftRotation(z.left);
                z = rightRotation(z);
            }
        }
        return z;
    }

    private AVLNode<V> findSuccessor(AVLNode<V> root) {
        AVLNode<V> p = root.right;
        while (p.left != null) {
            p = p.left;
        }
        return p;
    }

    public void draw() {
//        draw_NLR(this.root, "", "");
        draw_NRL(this.root, "", "");
    }


    public void draw_NRL(AVLNode<V> node, String prefix, String childrenPrefix) {
        if (node == null) {
            return;
        }
        System.out.println(prefix + node.key);
        if (node.left == null) {
            draw_NRL(node.right, childrenPrefix + "R-- ", childrenPrefix + "    ");
        } else {
            draw_NRL(node.right, childrenPrefix + "R-- ", childrenPrefix + "|   ");
            draw_NRL(node.left, childrenPrefix + "L-- ", childrenPrefix + "    ");
        }
    }

}
