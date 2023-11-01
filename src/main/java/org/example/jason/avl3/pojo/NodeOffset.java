package org.example.jason.avl3.pojo;

import lombok.Data;
import org.example.jason.avl3.structure.AVLNode;


/**
 * typedef std::pair<ItemListInterface*, Offset> ItemPtr;
 */
@Data
public class NodeOffset {
    AVLNode node;

    Integer offset;

    public NodeOffset() {
    }

    public NodeOffset(AVLNode node, Integer offset) {
        this.node = node;
        this.offset = offset;
    }
}
