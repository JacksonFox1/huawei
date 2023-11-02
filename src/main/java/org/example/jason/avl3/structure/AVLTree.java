package org.example.jason.avl3.structure;

import org.example.jason.avl3.pojo.CharacterMessage;
import org.example.jason.avl3.pojo.Gid;
import org.example.jason.avl3.pojo.NodeOffset;

import java.util.HashMap;
import java.util.Map;

public class AVLTree {
    AVLNode root;

    //暂未使用，后续开发
    Map<Gid, NodeOffset> gidToNodeOffset;

    public AVLTree() {
        root = null;
        gidToNodeOffset = new HashMap<>();
    }

    public int getHeight(AVLNode node) {
        if (node == null)
            return 0;
        return node.getHeight();
    }

    public int getTreeSize(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.getTreeSize();
    }

    public int getAllDataSize(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.getAllDataSize();
    }

    public int getValidDataSize(AVLNode node){
        if (node == null) {
            return 0;
        }
        return node.getValidDataSize();
    }

    public int getBalance(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.right) - getHeight(node.left);
    }

    int max(int a, int b) {
        return Math.max(a, b);
    }


    public NodeOffset begin() {
        if (root == null) {
            return null;
        }
        AVLNode node = root;
        while (node.left != null) {
            node = node.left;
        }
        NodeOffset response = new NodeOffset();

        while (true) {
            if (node.getValidDataSize() > 0) {
                response = node.getValidByIndex(0);
                return response;
            } else {
                node = getSuccessorNode(node);
            }
        }
    }

    public NodeOffset end() {
        if (root == null) {
            return null;
        }
        AVLNode node = root;
        while (node.right != null) {
            node = node.right;
        }
        NodeOffset response = new NodeOffset();

        while (true) {
            if (node.getValidDataSize() > 0) {
                response = node.getValidByIndex(node.validDataSize - 1);
                return response;
            } else {
                node = getPredecessorNode(node);
            }
        }
    }

    private void update(AVLNode node) {
        //如果正好传入的节点是root的父节点
        if (node == null) {
            return;
        }
        node.setHeight(max(getHeight(node.left), getHeight(node.right)));
        node.setTreeSize(getTreeSize(node.left) + getTreeSize(node.right));

        AVLNode p = node.parent;
        while (p != null) {
            p.setHeight(max(getHeight(p.left), getHeight(p.right)));
            p.setTreeSize(getTreeSize(p.left) + getTreeSize(p.right));
            p = p.parent;
        }
    }

    private void fullRebalance(AVLNode node) {
        if (node == null) {
            return;
        }
        rebalance(node, node.parent);
        AVLNode p = node.parent;
        while (p != null) {
            rebalance(p, p.parent);
            p = p.parent;
        }
    }

    public AVLNode leftRotation(AVLNode node) {
        //维护根节点
        boolean isRoot = false;
        if (node == this.root) {
            isRoot = true;
        }

        AVLNode right = node.right;
        AVLNode rightLeft = right.left;

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

    public AVLNode rightRotation(AVLNode node) {
        //维护根节点
        boolean isRoot = false;
        if (node == this.root) {
            isRoot = true;
        }

        AVLNode left = node.left;
        AVLNode leftRight = left.right;

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

    private AVLNode rebalance(AVLNode node, AVLNode parent) {
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
            //更新新节点的parent
            node.parent = parent;
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
            //更新新节点的parent
            node.parent = parent;
        }

        return node;
    }


    /**
     * 三种插入情况：
     * * 找到了待插入的Node，发现Gid和该Node的末尾字符是连续的，直接修改Node属性
     * * 找到了待插入的Node，发现pos正好处于末尾字符的下一位，但是Gid不连续，此时找到该Node的后继Node，并在其左子树上new新Node，并对树结构进行维护
     * * 找到了待插入的Node，发现pos正好处于末尾字符中间，分裂原Node为a，b，将b插入到下一个位置，维护树的平衡，并再new一个新节点c，再插入到a的下一个位置，维护书店
     * 维护树的平衡
     * 之所以不采用递归的方式进行插入，是因为后续存在根据Gid找到节点的需求，递归从根本上无法实现
     *
     * 可能出现的问题：可能在某个节点的第一个字符处插入
     * @param pos
     */
    public void insertByPos(int pos, CharacterMessage message) {
        if (root == null) {
            root = new AVLNode(message);
        }
        AVLNode eden;

        //如果是在文章开头插入
        if (pos == 0) {
            AVLNode beginNode = getNodeByPos(root, pos);
            eden = new AVLNode(message);
            beginNode.left = eden;
            eden.parent = beginNode;
            update(beginNode);
            rebalance(beginNode, beginNode.parent);
            return;
        }
        //找到上一个字符的所处的Node和NodeOffset
        AVLNode preCharacterNode = getNodeByPos(root, pos - 1);
        NodeOffset preCharacterNodeOffset = getNodeOffsetByPos(pos - 1);
        //获取上一个字符的Counter（无论是否被删除）
        int preCounter = preCharacterNode.getGidCounterByOffset(preCharacterNodeOffset.getOffset());
        int messageCounter = message.getGid().getCounter();
        //如果两个Gid是连续的
        if (preCounter == messageCounter - 1) {
            //如果数组没满
            if (preCharacterNode.getAllDataSize() < AVLNode.SPACE_SIZE) {
                preCharacterNode.insertCharacter(preCharacterNodeOffset.getOffset() + 1, message);
                //如果数组是满的，则需要new一个新的Node并插入到下一个位置
            } else {
                eden = new AVLNode(message);
                //上一个字符所在Node的后继Node
                AVLNode preNextNode = getSuccessorNode(preCharacterNode);
                preNextNode.left = eden;
                eden.parent = preNextNode;
                //更新size和height
                update(preNextNode);
                fullRebalance(preNextNode);
            }
            //如果两个Gid是不连续的
        } else {
            //如果恰好上一个字符是其所处的字符的末尾，new一个新节点并放在应该所处的位置
            //对应待插入字符要插入到某个Node的数组中的第一个位置
            if (preCharacterNode.getAllDataSize() - 1 == preCharacterNodeOffset.getOffset()) {
                eden = new AVLNode(message);

                //上一个字符所在Node的后继Node
                AVLNode preNextNode = getSuccessorNode(preCharacterNode);
                preNextNode.left = eden;
                eden.parent = preNextNode;
                //更新size和height
                update(preNextNode);
                fullRebalance(preNextNode);

            //如果要插入的位置处于Node的字符的中间，则Node分裂，更新原Node，并将分裂后的节点插入到原来节点的下一个节点，
            //再将该字符new一个新的节点
            } else {
                int begin = preCharacterNodeOffset.getOffset();
                int end = preCharacterNode.getAllDataSize();
                AVLNode backSplitNode = preCharacterNode.subAVLNode(begin, end);
                preCharacterNode.frontSplit(begin);
                //更新自该Node到root的所有节点的TreeSize
                update(preCharacterNode);
                AVLNode nextNode = getSuccessorNode(preCharacterNode);
                nextNode.left = backSplitNode;
                backSplitNode.parent = nextNode;
                update(nextNode);
                fullRebalance(nextNode);

                eden = new AVLNode(message);
                nextNode = getSuccessorNode(preCharacterNode);
                nextNode.left = eden;
                eden.parent = nextNode;
                update(nextNode);
                fullRebalance(nextNode);
            }
        }
    }

    /**
     * 假性删除，即修改删除标记位置
     * @param pos
     */
    public void deleteByPos(int pos) {
        AVLNode node = getNodeByPos(root, pos);
        int validIndex = pos - node.left.getTreeSize();
        NodeOffset nodeOffset = node.getValidByIndex(validIndex);
        node.deleteCharacterByOffset(nodeOffset.getOffset());

        AVLNode p = node.parent;
        while (p != null) {
            p.setTreeSize(p.getTreeSize() - 1);
            p = p.parent;
        }

        update(node.parent);
//        if (node.deleteCharacterByOffset(nodeOffset.getOffset())) {
//            System.out.println("删除成功");
//        } else {
//            System.out.println("删除失败");
//        }
    }


    /**
     * 对外测试提供的接口，对应 virtual ItemPtr getItemByPos(Index index) const = 0;
     * @param pos
     * @return
     */
    public NodeOffset getNodeOffsetByPos(int pos) {
        AVLNode node = getNodeByPos(root, pos);
        NodeOffset response;
        int validIndex = pos - node.left.getTreeSize();
        response = node.getValidByIndex(validIndex);
        return response;
    }

    /**
     *
     * @param node
     * @param pos 可能为0.为整个文章中的位置
     * @return
     */
    public AVLNode getNodeByPos(AVLNode node, int pos) {
        if (node == null) {
            return null;
        }
        int curValidDataSize = getValidDataSize(node);
        int leftTreeSize = getTreeSize(node.left);
        if (pos >= leftTreeSize && pos <= leftTreeSize + curValidDataSize - 1) {
            return node;
        } else if (pos < leftTreeSize) {
            return getNodeByPos(node.left, pos);
        } else {
            return getNodeByPos(node.right, pos - leftTreeSize - curValidDataSize);
        }
    }


    /**
     * 返回直接后继字符，无论是否删除
     * @param nodeOffset
     * @return
     */
    public NodeOffset successor(NodeOffset nodeOffset) {
        if (nodeOffset.getNode() == null) {
            return null;
        }
        AVLNode node = nodeOffset.getNode();
        int offset = nodeOffset.getOffset();

        NodeOffset response = new NodeOffset();
        int resOffset = 0;
        //获取node内数组的所有元素个数
        int allSize = node.getAllDataSize();

        //如果offset不是最后一个
        if (offset < allSize - 1) {
            int index;
            for (index = offset + 1; index < allSize; ++index) {
                if (!node.isDeleted(index)) {
                    response.setOffset(index);
                    response.setNode(node);
                    return response;
                }
            }
            node = this.getSuccessorNode(node);
            response = successor(new NodeOffset(node, -1));
        } else {
            node = this.getSuccessorNode(node);
            response = successor(new NodeOffset(node, -1));
        }

        return response;
    }

    public NodeOffset predecessor(NodeOffset nodeOffset) {
        if (nodeOffset.getNode() == null) {
            return null;
        }
        AVLNode node = nodeOffset.getNode();
        int offset = nodeOffset.getOffset();

        NodeOffset response = new NodeOffset();
        int resOffset = 0;
        //获取node内数组的所有元素个数
        int allSize = node.getAllDataSize();
        if (offset > 0) {
            int index;
            for (index = offset - 1; offset >= 0; offset--) {
                if (!node.isDeleted(offset)) {
                    response.setNode(node);
                    response.setOffset(index);
                    return response;
                }
            }
            node = getPredecessorNode(node);
            response = predecessor(new NodeOffset(node, node.getAllDataSize()));
        } else {
            node = getPredecessorNode(node);
            response = predecessor(new NodeOffset(node, node.getAllDataSize()));
        }
        return response;
    }

    /**
     *
     * @param node
     * @return
     */
    public AVLNode getSuccessorNode(AVLNode node) {
        if (node.right != null) {
            node = node.right;
            while (node.left != null) {
                node = node.left;
            }
        } else {
            while (node.parent.left != node) {
                node = node.parent;
                if (node.parent == null) {
                    break;
                }
            }
            node = node.parent;
        }
        return node;
    }

    public AVLNode getPredecessorNode(AVLNode node) {
        if (node.left != null) {
            node = node.left;
            while (node.right != null) {
                node = node.right;
            }
        } else {
            while (node.parent.right != node) {
                node = node.parent;
                if (node.parent == null) {
                    break;
                }
            }
            node = node.parent;
        }
        return node;
    }
}
