package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.UserVehicleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVehicleConfigRepository extends JpaRepository<UserVehicleConfig, Long> {

    List<UserVehicleConfig> findByUserIdAndVehicleName(long userId, String vehicleName);
}
