package org.example.jason.bPlusPl;

import lombok.Data;

@Data
public class Node {
    private Character content;
    private boolean deleted;


    public Node(Character content, boolean deleted) {
        this.content = content;
        this.deleted = deleted;
    }
}
