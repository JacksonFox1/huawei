package org.example.jason.avl3.pojo;


import lombok.Data;

/**
 * 客户端发送来的数据格式
 */
@Data
public class CharacterMessage {
    Character character;
    Gid gid;
    Gid leftOriginGid;
    Gid rightOriginGid;

}
