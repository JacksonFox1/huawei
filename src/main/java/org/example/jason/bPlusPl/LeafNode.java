package org.example.jason.bPlusPl;

import lombok.Data;

import java.util.List;

@Data
public class LeafNode extends TreeNode {
    private GID gid;
    private List<Node> nodeList;
    private LeafNode leftPtr;
    private LeafNode rightPtr;
    private GID originLeft;
    private GID originRight;

    public LeafNode() {
    }

    public LeafNode(GID gid, List<Node> nodeList, InternalNode parent, LeafNode leftPtr, LeafNode rightPtr) {
        this.gid = gid;
        this.nodeList = nodeList;
        this.parent = parent;
        this.leftPtr = leftPtr;
        this.rightPtr = rightPtr;
    }

    public void copy(LeafNode node) {
        this.gid = node.getGid();
        this.nodeList = node.getNodeList();
        this.parent = node.getParent();
        this.leftPtr = node.getLeftPtr();
        this.rightPtr = node.getRightPtr();
    }

    @Override
    public String toString() {
        return "LeafNode{" +
                "gid=" + gid +
                "nodeList=" + nodeList +
                '}';
    }
}
