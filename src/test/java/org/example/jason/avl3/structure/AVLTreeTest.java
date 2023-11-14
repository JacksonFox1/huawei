package org.example.jason.avl3.structure;

import org.example.jason.avl3.pojo.CharacterMessage;
import org.example.jason.avl3.pojo.NodeOffset;
import org.example.jason.avl3.test.GidFactory;
import org.junit.*;

public class AVLTreeTest {

    CharacterMessage message;
    GidFactory gidFactory;
    AVLTree tree;

    @BeforeClass
    public static void beforeClass() throws Exception {
//        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() throws Exception {
//        System.out.println("afterClass");
    }

    @Before
    public void setUp() throws Exception {
        gidFactory = new GidFactory();
        tree = new AVLTree();
//        System.out.println("setUp");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println();
        System.out.println("success");
    }

    /**
     * 测试追加插入元素和删除元素
     */
    @Test
    public void testInsert1() {
        //测试追加插入元素
        for (int i = 0; i < 20; ++i) {
            message = new CharacterMessage((char) (i % 26 + 97), gidFactory.produceGid());
            tree.insertByPos(i, message);
//            if (i % 3 == 2) {
//                tree.draw(tree.getRoot(), new AVLNode());
//            }
        }
        tree.draw(tree.getRoot(), new AVLNode());

        tree.insertByPos(0, new CharacterMessage((char) (25 + 97), gidFactory.produceGid()));
        tree.draw(tree.getRoot(), new AVLNode());

        for (int i = 0; i < 3; ++i) {
            tree.deleteByPos(0);
            tree.draw(tree.getRoot(), new AVLNode());
        }
        tree.deleteByPos(3);
        tree.draw(tree.getRoot(), new AVLNode());


        for (int i = 0; i < 16; ++i) {
            message = new CharacterMessage( (char)(i % 26 + 97), gidFactory.produceGid());
            tree.insertByPos(i, message);
        }
        tree.draw(tree.getRoot(), new AVLNode());
    }

    /**
     * 测试遍历文章
     */
    @Test
    public void testTraverse() {

        for (int i = 0; i < 26; ++i) {
            message = new CharacterMessage((char) (i % 26 + 97), gidFactory.produceGid());
            tree.insertByPos(i, message);
        }
        tree.draw(tree.getRoot(), new AVLNode());

        tree.deleteByPos(2);
        tree.deleteByPos(2);
        tree.deleteByPos(23);
        tree.draw(tree.getRoot(), new AVLNode());

        NodeOffset nodeOffset = tree.begin();
        NodeOffset end = tree.end();
        //这一步是必要的，因为有偏移的存在！！
        end.setOffset(end.getOffset() - 1);
        int counter = 0;
        while (!nodeOffset.equals(end)) {
            counter++;
            System.out.print(nodeOffset.getCharacter());
            nodeOffset = tree.successor(nodeOffset);
        }

//        do {
//            counter++;
//            System.out.print(nodeOffset.getCharacter());
//            nodeOffset = tree.successor(nodeOffset);
//
//        } while (!nodeOffset.equals(end));

        System.out.print(nodeOffset.getCharacter());
    }

    /**
     * 测试节点分裂
     */
    @Test
    public void testNodeSplit() {

        int size = 3;
        CharacterMessage[] characterMessage = new CharacterMessage[size];
        for (int i = 0; i < size; ++i) {
            message = new CharacterMessage( (char)(i % 26 + 97), gidFactory.produceGid());
            characterMessage[i] = message;
        }
        AVLNode rawNode = new AVLNode(characterMessage);
        AVLNode splitNode = rawNode.subAVLNode(1, 3);
        rawNode.frontSplit(1);
    }

    /**
     * 测试插入到某个节点的字符串的初始位置，此时new一个新节点
     */
    @Test
    public void test() {

        for (int i = 0; i < 18; ++i) {
            message = new CharacterMessage( (char)(i % 26 + 97), gidFactory.produceGid());
            tree.insertByPos(i, message);
        }
        tree.draw(tree.getRoot(), new AVLNode());

        //在j、k之间插入a，kl会分裂出去
        tree.insertByPos(10, new CharacterMessage( (char)(97), gidFactory.produceGid()));
        tree.draw(tree.getRoot(), new AVLNode());
    }

}