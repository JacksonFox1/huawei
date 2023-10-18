package org.example.jason;

public class AVLTree<V> {
    private AVLTreeNode mRoot;    // 根结点

    // AVL树的节点(内部类)
    class AVLTreeNode<V> {
        int size;           // 树的大小
        int height;         // 高度
        V ptr;              //  指向链表节点的指针
        AVLTreeNode left;    // 左孩子
        AVLTreeNode right;    // 右孩子
    }

    // 构造函数
    public AVLTree() {
        mRoot = null;
    }

    /*
     * 获取树的高度
     */
    private int height(AVLTreeNode tree) {
        if (tree != null)
            return tree.height;

        return 0;
    }

    public int height() {
        return height(mRoot);
    }

    /*
     * 比较两个值的大小
     */
    private int max(int a, int b) {
        return a > b ? a : b;
    }

    /*
     * (递归实现)查找"AVL树x"中第num个节点
     */
    private AVLTreeNode<V> getNode(AVLTreeNode<V> node, int num) {
        if (node == null) {
            return null;
        }
        int lSize = Size(node.left);
        if (lSize + 1 == num) {
            return node;
        } else if (lSize >= num) {
            return getNode(node.left, num);
        } else {
            return getNode(node.right, num - lSize - 1);
        }
    }

    public AVLTreeNode<V> getNode(int num) {
        return getNode(mRoot, num);
    }

    /*
     * 返回节点大小
     */
    private int Size(AVLTreeNode<V> node) {
        if(node == null) {
            return 0;
        }
        return node.size;
    }

    private void update(AVLTreeNode<V> node) {
        node.size = Size(node.left) + Size(node.right) + 1;
    }

    /*
     * 查找最小结点：返回tree为根结点的AVL树的最小结点。
     */
    private AVLTreeNode<V> minimum(AVLTreeNode<V> tree) {
        if (tree == null)
            return null;

        while(tree.left != null)
            tree = tree.left;

        return tree;
    }

    /*
     * 查找最大结点：返回tree为根结点的AVL树的最大结点。
     */
    private AVLTreeNode<V> maximum(AVLTreeNode<V> tree) {
        if (tree == null)
            return null;

        while(tree.right != null)
            tree = tree.right;

        return tree;
    }

    /*
     * LL：左左对应的情况(左单旋转)。
     *
     * 返回值：旋转后的根节点
     */
    private AVLTreeNode<V> leftLeftRotation(AVLTreeNode<V> k2) {
        AVLTreeNode<V> k1;

        k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;

        k2.height = max( height(k2.left), height(k2.right)) + 1;
        k1.height = max( height(k1.left), k2.height) + 1;

        update(k1);
        update(k2);

        return k1;
    }

    /*
     * RR：右右对应的情况(右单旋转)。
     *
     * 返回值：旋转后的根节点
     */
    private AVLTreeNode<V> rightRightRotation(AVLTreeNode<V> k1) {
        AVLTreeNode<V> k2;

        k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;

        k1.height = max( height(k1.left), height(k1.right)) + 1;
        k2.height = max( height(k2.right), k1.height) + 1;

        update(k1);
        update(k2);

        return k2;
    }

    /*
     * LR：左右对应的情况(左双旋转)。
     *
     * 返回值：旋转后的根节点
     */
    private AVLTreeNode<V> leftRightRotation(AVLTreeNode<V> k3) {
        k3.left = rightRightRotation(k3.left);
        return leftLeftRotation(k3);
    }

    /*
     * RL：右左对应的情况(右双旋转)。
     *
     * 返回值：旋转后的根节点
     */
    private AVLTreeNode<V> rightLeftRotation(AVLTreeNode<V> k1) {
        k1.right = leftLeftRotation(k1.right);
        return rightRightRotation(k1);
    }

    /*
     * 将结点插入到AVL树中，并返回根节点
     *
     * 参数说明：
     *     tree AVL树的根结点
     *     num 插入的结点在AVL树中的地位
     *     ptr 链表指针
     * 返回值：
     *     根节点
     */
    private AVLTreeNode<V> insert(AVLTreeNode<V> tree, int num, V ptr) {
        if (tree == null) {
            // 新建节点
            tree = new AVLTreeNode<V>();
            tree.size = 1;
            tree.ptr = ptr;
            tree.height = 0;
            tree.left = tree.right = null;
        } else {
            int lSize = Size(tree.left);

            if (num <= lSize + 1) {    // 应该插入到"tree的左子树"的情况
                tree.left = insert(tree.left, num, ptr);
                // 插入节点后，若AVL树失去平衡，则进行相应的调节。
                if (height(tree.left) - height(tree.right) == 2) {
                    int tt = Size(tree.left) - Size(tree.left.right);
                    if (num <= tt)
                        tree = leftLeftRotation(tree);
                    else
                        tree = leftRightRotation(tree);
                }
            } else  {    // 应该将key插入到"tree的右子树"的情况
                tree.right = insert(tree.right, num - lSize - 1, ptr);
                // 插入节点后，若AVL树失去平衡，则进行相应的调节。
                if (height(tree.right) - height(tree.left) == 2) {
                    int tt = Size(tree.right) - Size(tree.right.right);
                    if (num > tt)
                        tree = rightRightRotation(tree);
                    else
                        tree = rightLeftRotation(tree);
                }
            }
        }

        tree.height = max( height(tree.left), height(tree.right)) + 1;
        update(tree);

        return tree;
    }

    public void insert(int num, V ptr) {
        mRoot = insert(mRoot, num, ptr);
    }

    /*
     * 删除结点(z)，返回根节点
     *
     * 参数说明：
     *     tree AVL树的根结点
     *     num 删除第num个结点
     * 返回值：
     *     根节点
     */
    private AVLTreeNode<V> remove(AVLTreeNode<V> tree, int num) {
        // 根为空 或者 没有要删除的节点，直接返回null。
        if (tree == null || num < 0)
            return null;
        System.out.println("hh "+ tree.ptr + " " + num);

        int cmp = num - (Size(tree) - Size(tree.right));
        if (cmp < 0) {        // 待删除的节点在"tree的左子树"中
            tree.left = remove(tree.left, num);
            // 删除节点后，若AVL树失去平衡，则进行相应的调节。
            if (height(tree.right) - height(tree.left) == 2) {
                AVLTreeNode<V> r =  tree.right;
                if (height(r.left) > height(r.right))
                    tree = rightLeftRotation(tree);
                else
                    tree = rightRightRotation(tree);
            }
        } else if (cmp > 0) {    // 待删除的节点在"tree的右子树"中
            tree.right = remove(tree.right, cmp);
            // 删除节点后，若AVL树失去平衡，则进行相应的调节。
            if (height(tree.left) - height(tree.right) == 2) {
                AVLTreeNode<V> l =  tree.left;
                if (height(l.right) > height(l.left))
                    tree = leftRightRotation(tree);
                else
                    tree = leftLeftRotation(tree);
            }
        } else {    // tree是对应要删除的节点。
            // tree的左右孩子都非空
            if ((tree.left!=null) && (tree.right!=null)) {
                if (height(tree.left) > height(tree.right)) {
                    // 如果tree的左子树比右子树高；
                    // 则(01)找出tree的左子树中的最大节点
                    //   (02)将该最大节点的值赋值给tree。
                    //   (03)删除该最大节点。
                    // 这类似于用"tree的左子树中最大节点"做"tree"的替身；
                    // 采用这种方式的好处是：删除"tree的左子树中最大节点"之后，AVL树仍然是平衡的。
                    AVLTreeNode<V> max = maximum(tree.left);
                    tree.ptr = max.ptr;
                    tree.left = remove(tree.left, num - 1);
                    update(tree);
                } else {
                    // 如果tree的左子树不比右子树高(即它们相等，或右子树比左子树高1)
                    // 则(01)找出tree的右子树中的最小节点
                    //   (02)将该最小节点的值赋值给tree。
                    //   (03)删除该最小节点。
                    // 这类似于用"tree的右子树中最小节点"做"tree"的替身；
                    // 采用这种方式的好处是：删除"tree的右子树中最小节点"之后，AVL树仍然是平衡的。
                    AVLTreeNode<V> min = maximum(tree.right);
                    tree.ptr = min.ptr;
                    tree.right = remove(tree.right, 1);
                    update(tree);
                }
            } else {
                tree = (tree.left != null) ? tree.left : tree.right;
                update(tree);
            }
        }

        return tree;
    }

    public void remove(int num) {
        if (getNode(mRoot, num) != null)
            mRoot = remove(mRoot, num);
    }

    /*
     * 销毁AVL树
     */
    private void destroy(AVLTreeNode<V> tree) {
        if (tree==null)
            return ;

        if (tree.left != null)
            destroy(tree.left);
        if (tree.right != null)
            destroy(tree.right);

        tree = null;
    }

    public void destroy() {
        destroy(mRoot);
    }
}
