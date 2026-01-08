package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    List<UserSetting> findByUserId(long userId);

    List<UserSetting> findByUserIdAndGroupName(long userId, String groupName);

    Optional<UserSetting> findByUserIdAndGroupNameAndName(long userId, String groupName, String name);

    void deleteByUserIdAndGroupName(long userId, String groupName);

    void deleteByUserIdAndGroupNameAndName(long userId, String groupName, String name);
}
