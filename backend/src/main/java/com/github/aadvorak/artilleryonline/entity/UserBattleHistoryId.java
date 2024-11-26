package com.github.aadvorak.artilleryonline.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
@Embeddable
public class UserBattleHistoryId implements Serializable {

    @Column
    private long battleHistoryId;

    @Column
    private long userId;
}
