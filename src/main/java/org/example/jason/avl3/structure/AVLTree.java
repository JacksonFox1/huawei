package org.example.jason.avl3.structure;

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

    public int getAllSize(AVLNode node) {
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

    /**
     * 假性删除，即修改删除标记位置
     * @param pos
     */
    public deleteByPos(int pos) {
        
    }

    public NodeOffset getNodeByPos(int pos) {
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
