package org.example.jason.bPlusPl;

import lombok.Data;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Operations {
    private static final int MAX_INTERNAL_SIZE = 5;

    public InternalNode root;

    @Data
    private static class Pair {
        private int remain;
        private LeafNode node;

        public Pair(int remain, LeafNode node) {
            this.remain = remain;
            this.node = node;
        }
    }

    private static Pair getShiftByPos(int pos, InternalNode root, Deque<Integer> path) {
        // root为空
        if (root.getOffsetList().size() == 0) {
            return new Pair(0, null);
        }
        // 剩余量，用以定位在叶子节点中的坐标
        int remain = pos + 1;
        // 最终指向pos所在的叶子节点
        TreeNode cursor = root;
        while (cursor instanceof InternalNode) {
            final List<Integer> offsetList = ((InternalNode) cursor).getOffsetList();
            int i = 0;
            for (; i < offsetList.size(); i++) {
                remain -= offsetList.get(i);
                if (remain <= 0) {
                    // 剩余量小于等于0，pos在该下标的子树中
                    remain += offsetList.get(i);
                    break;
                }
            }
            // 如果遍历完了，说明插在最末尾
            i = i == offsetList.size() ? i - 1 : i;
            // 记录查找路径上所有offset的下标
            path.push(i);
            cursor = ((InternalNode) cursor).getChildren().get(i);
        }
        return new Pair(remain, (LeafNode) cursor);
    }

    public static Node getNodeByPos(int pos, InternalNode root) {
        Deque<Integer> path = new LinkedList<>();
        final Pair pair = getShiftByPos(pos, root, path);
        return pair.getNode().getNodeList().get(pair.getRemain() - 1);
    }

    public void insert(int pos, Character[] contents, GID gid) {
        Deque<Integer> path = new LinkedList<>();
        final Pair pair = getShiftByPos(pos, root, path);
        LeafNode originLeaf = pair.getNode(); // pos对应的叶子节点
        int splitIdx = pair.getRemain() - 1; // pos在对应叶子节点内的下标

        // 初始化插入节点和分裂origin产生的右节点
        List<Node> nodeList = new LinkedList<>();
        for (char content : contents) {
            nodeList.add(new Node(content, false));
        }
        LeafNode insertLeaf = new LeafNode();
        insertLeaf.setGid(gid);
        insertLeaf.setNodeList(nodeList);

        LeafNode rightLeaf = null;

        if (originLeaf == null) {
            // 树为空，插入节点为首个叶子
            insertLeaf.setLeftPtr(null);
            insertLeaf.setRightPtr(null);
            insertLeaf.setParent(root);

            root.getOffsetList().add(contents.length);
            root.getChildren().add(insertLeaf);
            root.setOffsetSum(contents.length);
        } else {

            InternalNode parent = originLeaf.getParent();
            insertLeaf.setParent(parent);

            if (splitIdx == 0) {
                // originLeaf首部插入，故无需分裂：originLeaf.leftPtr <--> insertLeaf <--> originLeaf
                insertLeaf.setLeftPtr(originLeaf.getLeftPtr());
                insertLeaf.setRightPtr(originLeaf);

                updateLeafParent(insertLeaf, originLeaf, null, parent, path.peek(), contents.length);
            } else {
                insertLeaf.setLeftPtr(originLeaf);
                if (splitIdx == originLeaf.getNodeList().size()) {
                    // originLeaf末尾插入，故无需分裂出右节点: originLeaf <--> insertLeaf <--> originLeaf.rightPtr
                    insertLeaf.setRightPtr(originLeaf.getRightPtr());
                } else {
                    // 在originLeaf中间插入，需进行分裂: originLeaf[:splitIdx) <--> insertLeaf <--> originLeaf[splitIdx:) <--> originLeaf.rightPtr
                    rightLeaf = new LeafNode(
                            new GID(originLeaf.getGid().getClientId(), originLeaf.getGid().getCounter() + splitIdx),
                            new LinkedList<>(originLeaf.getNodeList().subList(splitIdx, originLeaf.getNodeList().size())),
                            parent,
                            insertLeaf,
                            originLeaf.getRightPtr()
                    );
                    insertLeaf.setRightPtr(rightLeaf);
                    originLeaf.setNodeList(originLeaf.getNodeList().subList(0, splitIdx));
                }
                originLeaf.setRightPtr(insertLeaf);

                updateLeafParent(originLeaf, insertLeaf, rightLeaf, parent, path.peek(), contents.length);
            }

            // 向上更新父节点
            path.pop();
            insertInternal(parent, path, contents.length);

        }
    }

    /*
        更新父节点，在父节点对应的下标idx处做如下操作：
            1. idx对应leftLeaf
            2. idx+1对应middleLeaf
            3. idx+2对应原叶子节点中分裂出的rightLeaf（如果存在的话）
            4. 父节点的offsetSum+=contents.length
     */
    private void updateLeafParent(LeafNode leftLeaf, LeafNode middleLeaf, LeafNode rightLeaf, InternalNode parent, int updateIdx, int length) {
        parent.getOffsetList().set(updateIdx, leftLeaf.getNodeList().size());
        parent.getOffsetList().add(updateIdx + 1, middleLeaf.getNodeList().size());
        parent.getChildren().set(updateIdx, leftLeaf);
        parent.getChildren().add(updateIdx + 1, middleLeaf);
        parent.setOffsetSum(parent.getOffsetSum() + length);
        if (rightLeaf != null) {
            parent.getOffsetList().add(updateIdx + 2, rightLeaf.getNodeList().size());
            parent.getChildren().add(updateIdx + 2, rightLeaf);
        }
    }

    private void insertInternal(InternalNode cursor, Deque<Integer> path, int length) {
        if (cursor.getOffsetList().size() <= MAX_INTERNAL_SIZE) {
            // 无需分裂，只需要更新路径上下标所对应offset和offsetSum即可
            while (cursor.getParent() != null) {
                cursor = cursor.getParent();
                int updateIdx = path.pop();
                cursor.getOffsetList().set(updateIdx, cursor.getOffsetList().get(updateIdx) + length);
                cursor.setOffsetSum(cursor.getOffsetSum() + length);
            }
        } else {
            // 超过限制，需要分裂
            if (cursor.getParent() == null) {
                // cursor现在是根节点，分裂后形成新的根节点
                InternalNode newRoot = new InternalNode(
                        new LinkedList<>(),
                        new LinkedList<>(),
                        null
                );
                newRoot.getOffsetList().add(cursor.getOffsetSum());
                newRoot.getChildren().add(cursor);
                newRoot.setOffsetSum(cursor.getOffsetSum() - length);
                root = newRoot;
                cursor.setParent(newRoot);
                path.addFirst(0);
            }

            // 开始分裂操作
            // 分裂成左右两个内部节点
            int splitIdx = cursor.getOffsetList().size() / 2;
            InternalNode rightInt = new InternalNode(
                    new LinkedList<>(cursor.getOffsetList().subList(splitIdx, cursor.getOffsetList().size())),
                    new LinkedList<>(cursor.getChildren().subList(splitIdx, cursor.getChildren().size())),
                    cursor.getParent()
            );
            int offsetSum = 0; // 分裂后的原节点所含有的偏移总量
            for (int i = 0; i < splitIdx; i++) {
                offsetSum += cursor.getOffsetList().get(i);
            }
            rightInt.setOffsetSum(cursor.getOffsetSum() - offsetSum);
            for (TreeNode node : rightInt.getChildren()) {
                node.setParent(rightInt);
            }

            cursor.setOffsetList(new LinkedList<>(cursor.getOffsetList().subList(0, splitIdx)));
            cursor.setChildren(new LinkedList<>(cursor.getChildren().subList(0, splitIdx)));
            cursor.setOffsetSum(offsetSum);

            InternalNode parent = cursor.getParent();
            int updateIdx = path.pop();
            parent.getOffsetList().add(updateIdx + 1, rightInt.getOffsetSum());
            parent.getChildren().add(updateIdx + 1, rightInt);
            parent.getOffsetList().set(updateIdx, cursor.getOffsetSum());
            parent.setOffsetSum(parent.getOffsetSum() + length);

            insertInternal(parent, path, length);
        }
    }

    public void delete(int pos) {
        Deque<Integer> path = new LinkedList<>();
        final Pair pair = getShiftByPos(pos, root, path);
        LeafNode originLeaf = pair.getNode(); // pos对应的叶子节点
        int splitIdx = pair.getRemain() - 1; // pos在对应叶子节点内的下标

        InternalNode parent = originLeaf.getParent();
        LeafNode rightLeaf = null;
        originLeaf.getNodeList().get(splitIdx).setDeleted(true);
        if (splitIdx != originLeaf.getNodeList().size() - 1) {
            // 待删除点不在originLeaf的末尾，则原节点分裂出右节点，右节点包含splitIdx之后的部分
            rightLeaf = new LeafNode(
                    new GID(originLeaf.getGid().getClientId(), originLeaf.getGid().getCounter() + splitIdx + 1),
                    originLeaf.getNodeList().subList(splitIdx + 1, originLeaf.getNodeList().size()),
                    originLeaf.getParent(),
                    originLeaf,
                    originLeaf.getRightPtr());

            // 保留待删除点，仅对其进行标记
            originLeaf.setNodeList(originLeaf.getNodeList().subList(0, splitIdx + 1));
            originLeaf.setRightPtr(rightLeaf);
        }

        int updateIdx = path.pop();
        parent.getOffsetList().set(updateIdx, originLeaf.getNodeList().size() - 1);
        parent.setOffsetSum(parent.getOffsetSum() - 1);
        if (rightLeaf != null) {
            parent.getOffsetList().add(updateIdx + 1, rightLeaf.getNodeList().size());
            parent.getChildren().add(updateIdx + 1, rightLeaf);
        }

        insertInternal(parent, path, -1);
    }


    public static void display(TreeNode cursor) {
        if (cursor == null)
            return;
        Queue<TreeNode> deque = new LinkedList<>();
        deque.offer(cursor);

        while (!deque.isEmpty()) {
            int size = deque.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = deque.poll();
                if (node instanceof InternalNode) {
                    for (int val : ((InternalNode) node).getOffsetList()) {
                        System.out.print(val + " ");
                    }
                    System.out.print("|| ");

                    for (TreeNode v : ((InternalNode) node).getChildren()) {
                        deque.offer(v);
                    }
                } else {
                    for (Node n : ((LeafNode) node).getNodeList()) {
                        System.out.print(n.getContent());
                        System.out.print(n.isDeleted() ? '0' : '1');
                        System.out.print(" ");
                    }
                    System.out.print("|| ");
                }
            }
            System.out.println();
        }
    }
}
