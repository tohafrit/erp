package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Setting;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    Setting findFirstByCode(String code);
}