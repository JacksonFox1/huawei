package org.example.jason.avl3.pojo;

import lombok.Data;
import org.example.jason.avl3.structure.AVLNode;

import java.util.Objects;


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

    public Character getCharacter() {
        return this.node.getText()[this.offset];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeOffset)) return false;
        NodeOffset that = (NodeOffset) o;
        return Objects.equals(getNode(), that.getNode()) && Objects.equals(getOffset(), that.getOffset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNode(), getOffset());
    }
}
