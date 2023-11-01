package org.example.jason.avl3.pojo;

import lombok.Data;

@Data
public class CharacterInfo {
    //YATA算法需要的数据
    Gid leftOriginGid;
    //YATA算法需要的数据
    Gid rightOriginGid;
    //是否删除的标记，为true则为被删除（假删），为false则未被删除
    boolean isDeleted;

    public CharacterInfo() {
        isDeleted = false;
    }

    public CharacterInfo(CharacterMessage message) {
        this.leftOriginGid = message.getLeftOriginGid();
        this.rightOriginGid = message.getRightOriginGid();
        this.isDeleted = false;
    }
}
