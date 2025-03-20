package com.github.aadvorak.artilleryonline.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_vehicle_configs")
public class UserVehicleConfig {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long userId;

    @Column
    private String vehicleName;

    @Column
    private String name;

    @Column
    private String value;

    @Column
    private Integer amount;
}
