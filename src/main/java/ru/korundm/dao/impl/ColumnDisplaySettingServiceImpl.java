package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ColumnDisplaySettingService;
import ru.korundm.entity.ColumnDisplaySetting;
import ru.korundm.entity.User;
import ru.korundm.repository.ColumnDisplaySettingRepository;

import java.util.List;

@Service
@Transactional
public class ColumnDisplaySettingServiceImpl implements ColumnDisplaySettingService {

    private final ColumnDisplaySettingRepository columnDisplaySettingRepository;

    public ColumnDisplaySettingServiceImpl(ColumnDisplaySettingRepository columnDisplaySettingRepository) {
        this.columnDisplaySettingRepository = columnDisplaySettingRepository;
    }

    @Override
    public List<ColumnDisplaySetting> getAll() {
        return columnDisplaySettingRepository.findAll();
    }

    @Override
    public List<ColumnDisplaySetting> getAllById(List<Long> idList) {
        return columnDisplaySettingRepository.findAllById(idList);
    }

    @Override
    public ColumnDisplaySetting save(ColumnDisplaySetting object) {
        return columnDisplaySettingRepository.save(object);
    }

    @Override
    public List<ColumnDisplaySetting> saveAll(List<ColumnDisplaySetting> objectList) {
        return columnDisplaySettingRepository.saveAll(objectList);
    }

    @Override
    public ColumnDisplaySetting read(long id) {
        return columnDisplaySettingRepository.getOne(id);
    }

    @Override
    public void delete(ColumnDisplaySetting object) {
        columnDisplaySettingRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        columnDisplaySettingRepository.deleteById(id);
    }

    @Override
    public List<ColumnDisplaySetting> getColumnSettingList(String tableId, User user) {
        return columnDisplaySettingRepository.findByTableIdAndUserOrderByOrderAsc(tableId, user);
    }

    @Override
    public ColumnDisplaySetting getSettingByName(String tableId, String name, User user) {
        return columnDisplaySettingRepository.findFirstByTableIdAndNameAndUser(tableId, name, user);
    }

    @Override
    public void resetSettings(User user, String tableId) {
        columnDisplaySettingRepository.deleteAllByUserAndTableId(user, tableId);
    }
}