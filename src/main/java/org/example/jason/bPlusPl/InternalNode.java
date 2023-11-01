package org.example.jason.bPlusPl;

import lombok.Data;

import java.util.List;

@Data
public class InternalNode extends TreeNode{
    private List<Integer> offsetList;
    private List<TreeNode> children;
    private int offsetSum = 0;

    public InternalNode(List<Integer> offsetList, List<TreeNode> children, InternalNode parent) {
        this.offsetList = offsetList;
        this.children = children;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "InternalNode{}";
    }
}
