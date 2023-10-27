/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.jason.avl2.structure;

import java.util.HashMap;
import java.util.Map;

public class AVLTree<V> {
    /*
    建立gid到Node的引用的映射。
    Q:为什么不建立gid到pos的映射？
    A:因为当一个Node被标记为删除时，其pos失效，无法再通过pos查找到该Node，只能通过
    gid查找，因此必须建立一个gid和Node的映射
     */
    Map<Long, AVLNode<V> > gidToNode;

    //根节点的变动！！！
    AVLNode<V> root;

    public AVLTree() {
        root = null;
        gidToNode = new HashMap<>();
    }

    private int getHeight(AVLNode<V> node) {
        if (node == null)
            return 0;
        return node.height;
    }

    private int getSize(AVLNode<V> node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    private int max(int a, int b) {
        return Math.max(a, b);
    }

    /**
     * 重新计算该节点的高度，和size，并自下而上全部更新
     * @param node
     */
    private void update(AVLNode<V> node) {
        node.height = max(getHeight(node.left), getHeight(node.right));
        if (node.tombstone) {
            node.size = getSize(node.left) + getSize(node.right);
        } else {
            node.size = getSize(node.left) + getSize(node.right) + 1;
        }
        //有必要吗？？？
        AVLNode<V> p = node.parent;
        while (p != null) {
            p.height = max(getHeight(p.left), getHeight(p.right));
            if (p.tombstone) {
                p.size = getSize(p.left) + getSize(p.right);
            } else {
                p.size = getSize(p.left) + getSize(p.right) + 1;
            }
            p = p.parent;
        }

    }

    /**
     * 向右旋，对应LL不平衡
     * 注意旋转完之后，只是返回了正确的node节点，但是node节点的parent指针还未更新
     * @param node
     * @return
     */
    public AVLNode<V> rightRotation(AVLNode<V> node) {
        //维护根节点
        boolean isRoot = false;
        if (node == this.root) {
            isRoot = true;
        }

        AVLNode<V> left = node.left;
        AVLNode<V> leftRight = left.right;

        node.left = leftRight;
        leftRight.parent = node;

        left.right = node;
        node.parent = left;

        update(node);
        update(left);

        if (isRoot) {
            this.root = left;
        }
        return left;
    }

    /**
     * 左旋，对应RR不平衡
     * 注意旋转完之后，只是返回了正确的node节点，但是node节点的parent指针还未更新
     * @param node
     * @return
     */
    public AVLNode<V> leftRotation(AVLNode<V> node) {
        //维护根节点
        boolean isRoot = false;
        if (node == this.root) {
            isRoot = true;
        }

        AVLNode<V> right = node.right;
        AVLNode<V> rightLeft = right.left;

        node.right = rightLeft;
        rightLeft.parent = right;

        right.left = node;
        node.parent = right;

        //注意update的顺序，自底向上update
        update(node);
        update(right);

        if (isRoot) {
            this.root = right;
        }

        return right;
    }

    /**
     * 返回node的右子树高度与左子树高度之差，为正数则为右边高
     * @param node
     * @return
     */
    int getBalance(AVLNode<V> node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.right) - getHeight(node.left);
    }

    /**
     * 将gid对应的Node标记为删除
     * @param gid
     */
    public void tombstoned(Long gid) {
        AVLNode<V> node = getNodeByGid(gid);
        node.tombstone = true;
        node.size--;
        AVLNode<V> p = node.parent;
        while (p != null) {
            p.size--;
            p = p.parent;
        }
    }

    public void tombstoned(int pos) {
        AVLNode<V> node = getNodeByPos(pos);
        node.tombstone = true;
        node.size--;
        AVLNode<V> p = node.parent;
        while (p != null) {
            p.size--;
            p = p.parent;
        }
    }

    /**
     * 节点插入操作
     * @param node
     * @param value
     */
    private void insert(AVLNode<V> node, V value) {
        AVLNode<V> successor = findSuccessor(node);
        AVLNode<V> eden = new AVLNode<>(value);
        if (successor == node) {
            //恰好node节点的右子树为空
            successor.right = eden;
        } else {
            //插入到下一个位置
            successor.left = eden;
        }
        eden.parent = successor;

//        AVLNode<V> p = node;
        AVLNode<V> p = eden.parent;
        boolean heightUpdate = true;
        while (p != null) {
            while (heightUpdate) {
                int oldHeight = p.height;
                p.height = 1 + max(getHeight(p.left), getHeight(p.right));
                if (p.height == oldHeight) {
                    heightUpdate = false;
                }
            }
            p.size++;
            p = p.parent;
        }

        gidToNode.put(eden.gid, eden);

        rebalance(node, node.parent);
    }

    /**
     * 在gid节点后面插入
     * @param gid
     * @param value
     */
    public void insert(Long gid, V value) {
        AVLNode<V> node = getNodeByGid(gid);
        insert(node, value);
    }

    /**
     * 在pos后面插入
     * @param pos
     * @param value
     */
    public void insert(int pos, V value) {
        AVLNode<V> node = getNodeByPos(pos);
        insert(node, value);
    }

    private void delete(AVLNode<V> node) {
        if (node == null) {
            return;
        }
        //记得rebalance！！！
        Long gid = node.gid;
        //因为不是使用递归法删除，所以必须找到父节点，并判断是左孩子还是右孩子
        boolean isLeft;
        //注意parent可能为空
        AVLNode<V> parent = node.parent;

        //为空表示正在删除根节点
//      if (parent == null) {
        if (node == this.root) {
            //如果node没有右节点，返回本身；有右子树则返回下一个节点
            AVLNode<V> nextNode = findSuccessor(node);
            //找到根节点的左孩子
            AVLNode<V> rootLeft = node.left;
            //nextNode和node相等，表示右子树为空，正在删除节点本身
            if (nextNode == node) {
                //解除两个对原根节点的引用
                node.left = null;
                rootLeft.parent = null;
                this.root = rootLeft;
                gidToNode.remove(node.gid);
            } else {
                node.gid = nextNode.gid;
                node.values = nextNode.values;
                //删除对老节点的引用
                gidToNode.remove(gid);
                //注意下面两句顺序不能颠倒
                delete(nextNode);
                gidToNode.put(node.gid, node);
            }
        //并不是在删除根节点，把下一个节点顶替上来
        } else {
            AVLNode<V> nextNode = findSuccessor(node);
            if (parent.left == node) {
                isLeft = true;
            } else {
                isLeft = false;
            }
            //最终都会流向这个分支，所以只需要在这里rebalance
            if (node.left == null && node.right == null) {
                if (isLeft) {
                    node.parent.left = null;
                } else {
                    node.parent.right = null;
                }
                node.parent = null;
                update(parent);
                rebalance(parent, parent.parent);
                gidToNode.remove(gid);
            } else if (node.left == null) {
//                AVLNode<V> nodeRight = node.right;
//                parent.right = nodeRight;
//                nodeRight.parent = parent;
//                node.parent = null;
//                node.right = null;
                node.gid = nextNode.gid;
                node.values = nextNode.values;
                //删除对老节点的引用
                gidToNode.remove(gid);
                //注意下面两句顺序不能颠倒
                delete(nextNode);
                gidToNode.put(node.gid, node);
//                update(parent);
//                rebalance(parent, parent.parent);
//                gidToNode.remove(gid);
            } else if (node.right == null) {
                if (isLeft) {
                    parent.left = node.left;
                } else {
                    parent.right = node.left;
                }
                node.parent = null;
                gidToNode.remove(gid);
                update(parent);
                rebalance(parent, parent.parent);
            } else {
                node.gid = nextNode.gid;
                node.values = nextNode.values;
                //删除对老节点的引用
                gidToNode.remove(gid);
                //注意下面两句顺序不能颠倒
                delete(nextNode);
                gidToNode.put(node.gid, node);
            }
        }
    }

    public void delete(Long gid) {
        AVLNode<V> node = getNodeByGid(gid);
        delete(node);
    }

    public void delete(int pos) {
        AVLNode<V> node = getNodeByPos(pos);
        delete(node);
    }


    private AVLNode<V> rebalance(AVLNode<V> node, AVLNode<V> parent) {
        node.height = 1 + max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);
        if (balance > 1) {
            //如果右子树高
            if (getHeight(node.right.right) > getHeight(node.right.left)) {
                //如果为RR不平衡
                node = leftRotation(node);
            } else {
                //如果为RF不平衡
                node.right = rightRotation(node.right);
                node.right.parent = node;

                node = leftRotation(node);
            }
        } else if (balance < -1) {
            //如果左子树高
            if (getHeight(node.left.left) > getHeight(node.left.right)) {
                //如果是LL不平衡
                node = rightRotation(node);
            } else {
                //如果是LR不平衡
                node.left = leftRotation(node.left);
                node.left.parent = node;

                node = rightRotation(node);
            }
        }
        //更新新节点的parent
        node.parent = parent;
        return node;
    }

    /**
     * 由于根据pos进行插入，则进行中序遍历可以得到想要的顺序，在某个节点后面插入即
     * 在该节点的右节点的最左边的节点处插入新节点
     * @param node
     * @return 返回最左边的节点，如果该节点没有右子树，则返回本身
     */
    private AVLNode<V> findSuccessor(AVLNode<V> node) {
        AVLNode<V> p = node.right;
        if (p == null) {
            return node;
        }
        while (p.left != null) {
            p = p.left;
        }
        return p;
    }

    /**
     * 根据位置查找结点，被标记为墓碑的节点跳过
     * @param node
     * @param pos
     * @return
     */
    private AVLNode<V> getNodeByPos(AVLNode<V> node, int pos) {
        if (node == null) {
            return null;
        }
        int leftSize = getSize(node.left);
        if (leftSize + 1 == pos) {
            if (!node.tombstone) {
                return node;
            } else {
                //如果该节点被标记为墓碑节点，说明真实的节点在其右子树上
                return getNodeByPos(node.right, 1);
            }
        } else if (leftSize >= pos) {
            return getNodeByPos(node.left, pos);
        } else {
            if (node.tombstone) {
                return getNodeByPos(node.right, pos - leftSize);
            } else {
                return getNodeByPos(node.right, pos - (leftSize + 1));
            }
        }
    }

    public AVLNode<V> getNodeByPos(int pos) {
        return getNodeByPos(root, pos);
    }

    public int getPosByGid(Long gid) {
        AVLNode<V> node = getNodeByGid(gid);
        if (node != null) {
            return node.size;
        } else {
            System.out.println("该gid没有对应的pos");
            return 0;
        }
    }

    public AVLNode<V> getNodeByGid(Long gid) {
        AVLNode<V> node = null;
        if (gidToNode.containsKey(gid)) {
            node = gidToNode.get(gid);
        } else {
            System.out.println("该gid没有对应的node");
        }
        return node;
    }

//
//    public void draw() {
////        draw_NLR(this.root, "", "");
//        draw_NRL(this.root, "", "");
//    }
//
//
//    public void draw_NRL(AVLNode<V> node, String prefix, String childrenPrefix) {
//        if (node == null) {
//            return;
//        }
//        System.out.println(prefix + node.key);
//        if (node.left == null) {
//            draw_NRL(node.right, childrenPrefix + "R-- ", childrenPrefix + "    ");
//        } else {
//            draw_NRL(node.right, childrenPrefix + "R-- ", childrenPrefix + "|   ");
//            draw_NRL(node.left, childrenPrefix + "L-- ", childrenPrefix + "    ");
//        }
//    }

}
