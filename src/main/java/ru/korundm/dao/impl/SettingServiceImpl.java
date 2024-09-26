package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SettingService;
import ru.korundm.entity.Setting;
import ru.korundm.repository.SettingRepository;

import java.util.List;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;

    public SettingServiceImpl(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public List<Setting> getAll() {
        return settingRepository.findAll();
    }

    @Override
    public List<Setting> getAllById(List<Long> idList) { return settingRepository.findAllById(idList); }

    @Override
    public Setting save(Setting object) {
        return settingRepository.save(object);
    }

    @Override
    public List<Setting> saveAll(List<Setting> objectList) { return settingRepository.saveAll(objectList); }

    @Override
    public Setting read(long id) {
        return settingRepository.getOne(id);
    }

    @Override
    public void delete(Setting object) {
        settingRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        settingRepository.deleteById(id);
    }

    @Override
    public Setting getSettingByCode(String code) {
        return settingRepository.findFirstByCode(code);
    }
}