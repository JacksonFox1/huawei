package org.example.jason.avl3.structure;

import lombok.Data;
import org.example.jason.avl3.pojo.CharacterInfo;
import org.example.jason.avl3.pojo.CharacterMessage;
import org.example.jason.avl3.pojo.Gid;
import org.example.jason.avl3.pojo.NodeOffset;

@Data
public class AVLNode {
    static final int SPACE_SIZE = 10;
    //该节点初始元素的Gid
    Gid gid;
    //用于存储实际的字符数据，大小暂定为10
    Character[] text;
    //用于记录每个字符的信息，是否被删除，原始前驱和原始后继
    CharacterInfo[] informations;
    //该节点所在的高度，用于AVL树的平衡
    int height;
    //数组中合法数据的大小
    int validDataSize;
    //数组中所有的数据大小，即包括被删除的数据
    int allDataSize;

    //以该节点为根节点的树的有效值的个数!!用于根据pos对节点进行搜索
    int treeSize;
    //左节点
    AVLNode left;
    //右节点
    AVLNode right;
    //节点的父指针
    AVLNode parent;
    //客户端传来的数据。用于YATA算法，第一个字符的前驱
    Gid headOrigin;
    //客户端传来的数据。用于YATA算法，最后一个字符（无论是否被标记删除）的后继
    Gid endRightOrigin;

    /**
     * 通过message new一个新节点
     * @param message
     */
    public AVLNode(CharacterMessage message) {
        this.gid = message.getGid();
        this.text = new Character[SPACE_SIZE];
        this.informations = new CharacterInfo[SPACE_SIZE];

        this.text[0] = message.getCharacter();
        this.informations[0] = new CharacterInfo(message);

        this.headOrigin = message.getLeftOriginGid();
        this.endRightOrigin = message.getRightOriginGid();

        this.height = 1;
        this.validDataSize = 1;
        this.allDataSize = 1;
        this.treeSize = 1;
    }

    public AVLNode(CharacterMessage[] message) {
        this();
        if (message == null) {
            return;
        }

        int length = message.length;

        this.text = new Character[SPACE_SIZE];
        this.informations = new CharacterInfo[SPACE_SIZE];
        this.gid = message[0].getGid();
        this.headOrigin = message[0].getLeftOriginGid();
        this.endRightOrigin = message[length - 1].getRightOriginGid();

        for (int i = 0; i < length; ++i) {
            this.text[i] = message[i].getCharacter();
            this.informations[i] = new CharacterInfo(message[i]);
        }

        this.height = 1;
        this.validDataSize = length;
        this.allDataSize = length;
        this.treeSize = length;
    }

    /**
     * 创建一个空的节点
     */
    public AVLNode() {
        this.text = new Character[SPACE_SIZE];
        this.informations = new CharacterInfo[SPACE_SIZE];
        this.height = 1;
    }

    public AVLNode(Gid gid, Character character) {
        this.gid = gid;
        this.text = new Character[SPACE_SIZE];
        this.informations = new CharacterInfo[SPACE_SIZE];
        this.text[0] = character;
        this.informations[0] = new CharacterInfo();
        this.height = 1;
        this.validDataSize = 1;
        this.allDataSize = 1;
        this.treeSize = 1;
    }

    /**
     *
     * @param offset 传来的偏移量
     * @return
     */
    public boolean isDeleted(int offset) {
        if (offset < 0) {
            System.out.println("error! exception in isDeleted()");
        }
        return informations[offset].isDeleted();
    }


    /**
     * 接口待测试
     * 可能会出现死循环
     * @param validIndex 表示未被删除的节点，从0开始计数
     * @return
     */
    public NodeOffset getValidByIndex(int validIndex) {
        if (validIndex < 0) {
            return null;
        }
        //正常流程
        AVLNode node = this;
        NodeOffset nodeOffset = new NodeOffset();
        int offset = 0;
        while (validIndex >= 0) {
            if (isDeleted(offset++)) {
                continue;
            }
            validIndex--;
        }
        nodeOffset.setNode(this);
        nodeOffset.setOffset(offset - 1);

//        //如果该节点所有的值都被标记为删除
//        if (offset >= this.allDataSize) {
//            node = node.parent;
//
//        }

        return new NodeOffset(node, offset - 1);
    }

}
