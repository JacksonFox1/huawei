package org.example.jason.avl3.structure;

import lombok.Data;
import org.example.jason.avl3.pojo.CharacterInfo;
import org.example.jason.avl3.pojo.CharacterMessage;
import org.example.jason.avl3.pojo.Gid;
import org.example.jason.avl3.pojo.NodeOffset;

@Data
public class AVLNode {

    //设置3只是为了方便测试，可以设置为8、10、16或者其他
    static final int SPACE_SIZE = 3;
    //该节点初始元素的Gid，无论是否被删除
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
    //客户端传来的数据。用于YATA算法，最后一个字符（无论是否被标记删除）的后继，无论是否被删除
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
            System.out.println("error! exception occurred in isDeleted()");
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
            //由返回空对象改为返回无意义对象
            return new NodeOffset();
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

    public boolean deleteCharacterByOffset(int offset) {
        if (!informations[offset].isDeleted()) {
            informations[offset].setDeleted(true);
            this.setTreeSize(this.getTreeSize() - 1);
            this.setValidDataSize(this.getValidDataSize() - 1);
            return true;
        }
        return false;
    }

    public Gid getGidByOffset(int offset) {
        Gid gid = new Gid();
        gid.setClientId(this.gid.getClientId());
        gid.setCounter(this.gid.getCounter() + offset);
        return gid;
    }

    public int getGidCounterByOffset(int offset) {
        return this.gid.getCounter() + offset;
    }

    public boolean insertCharacter(int offset, CharacterMessage message) {
        text[offset] = message.getCharacter();
        informations[offset] = new CharacterInfo(message);
        this.treeSize++;
        this.validDataSize++;
        this.allDataSize++;
        this.endRightOrigin = message.getRightOriginGid();
        return true;
    }

    /**
     * 在节点分裂时更新自身，采用安全的方式,擦除[offset, SPACE_SIZE]的数据.
     * @param offset 假设为5，则保留下标为0至下标为4的节点
     */
    public void frontSplit(int offset) {
        this.endRightOrigin = this.computeRightOriginGid(offset - 1);
        this.treeSize = this.treeSize - this.allDataSize + offset;
        this.allDataSize = offset;

        int validDataSize = 0;
        for (int i = offset; i < SPACE_SIZE; ++i) {
            text[i] = null;
            informations[i] = null;
        }

        for (int i = 0; i < offset; ++i) {
            if (!isDeleted(i)) {
                validDataSize++;
            }
        }
        this.validDataSize = validDataSize;

    }

    /**
     * 获取一个节点的子节点，并不加入到树结构，即不会继承height，size等属性
     * 左开右闭，从0开始计数
     * @param begin
     * @param end
     * @return
     */
    public AVLNode subAVLNode(int begin, int end) {
        AVLNode eden = new AVLNode();
        Gid edenGid = new Gid();
        int validDataSize = 0;

        edenGid.setClientId(this.gid.getClientId());
        edenGid.setCounter(this.gid.getCounter() + begin);
        eden.setGid(edenGid);

        for (int i = begin; i < end; ++i) {
            if (!isDeleted(i)) {
                validDataSize++;
            }
            eden.text[i - begin] = this.text[i];
            eden.informations[i - begin] = this.informations[i];
        }
        eden.validDataSize = validDataSize;

        eden.height = 1;
        eden.allDataSize = end - begin;
        eden.treeSize = end - begin;
        eden.headOrigin = this.computeLeftOriginGid(begin);
        eden.endRightOrigin = this.computeRightOriginGid(end - 1);


        return eden;
    }

    public Gid computeLeftOriginGid(int offset) {
        if (offset == 0) {
            return this.headOrigin;
        } else {
            //获取初始元素的counter
            Integer counter = this.gid.getCounter();
            Integer clientId = this.gid.getClientId();
            counter = counter + offset - 1;
            return new Gid(clientId, counter);
        }
    }

    public Gid computeRightOriginGid(int offset) {
        if (offset == this.allDataSize - 1) {
            return this.endRightOrigin;
        } else {
            Integer counter = this.gid.getCounter();
            Integer clientId = this.gid.getClientId();
            counter = counter + offset + 1;
            return new Gid(clientId, counter);
        }
    }

}
