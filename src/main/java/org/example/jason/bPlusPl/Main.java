package org.example.jason.bPlusPl;

import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        InternalNode root = new InternalNode(
                new LinkedList<>(),
                new LinkedList<>(),
                null
        );
        Operations operations = new Operations();
        operations.root = root;
        for (int i = 0; i < 30; i++) {
            operations.insert(i, new Character[]{(char)(97+i), (char)(97+i), (char)(97+i)}, new GID(i, i));
            Operations.display(operations.root);
        }

        operations.delete(4);
        Operations.display(operations.root);
    }
}
