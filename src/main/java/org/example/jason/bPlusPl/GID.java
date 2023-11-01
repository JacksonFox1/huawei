package org.example.jason.bPlusPl;

import lombok.Data;

@Data
public class GID {
    private Integer clientId;
    private Integer counter;

    public GID(Integer clientId, Integer counter) {
        this.clientId = clientId;
        this.counter = counter;
    }
}
