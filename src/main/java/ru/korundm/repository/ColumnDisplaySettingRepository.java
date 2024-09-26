package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.entity.ColumnDisplaySetting;
import ru.korundm.entity.User;

import java.util.List;

public interface ColumnDisplaySettingRepository extends JpaRepository<ColumnDisplaySetting, Long> {

    List<ColumnDisplaySetting> findByTableIdAndUserOrderByOrderAsc(String tableId, User user);

    ColumnDisplaySetting findFirstByTableIdAndNameAndUser(String tableId, String name, User user);

    @Transactional
    void deleteAllByUserAndTableId(User user, String tableId);
}