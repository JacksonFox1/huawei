package org.example.jason.avl3.test;

import org.example.jason.avl3.pojo.Gid;

public class GidFactory {
    public static final Integer clientId = 123456;

    public static Integer counter = 0;

    public Gid produceGid() {
        return new Gid(clientId, counter++);
    }
}
