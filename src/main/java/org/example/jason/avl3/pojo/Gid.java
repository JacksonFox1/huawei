package org.example.jason.avl3.pojo;

import lombok.Data;

@Data
public class Gid {
    private Integer clientId;

    private Integer counter;

    public Gid(Integer clientId, Integer counter) {
        this.clientId = clientId;
        this.counter = counter;
    }

}
