package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.User1CService;
import ru.korundm.entity.User1C;
import ru.korundm.repository.User1CRepository;

import java.util.List;

@Service
@Transactional
public class User1CServiceImpl implements User1CService {

    private final User1CRepository user1CRepository;

    public User1CServiceImpl(User1CRepository user1CRepository) {
        this.user1CRepository = user1CRepository;
    }

    @Override
    public List<User1C> getAll() {
        return user1CRepository.findAll();
    }

    @Override
    public List<User1C> getAllById(List<Long> idList) {
        return user1CRepository.findAllById(idList);
    }

    @Override
    public User1C save(User1C object) {
        return user1CRepository.save(object);
    }

    @Override
    public List<User1C> saveAll(List<User1C> objectList) {
        return user1CRepository.saveAll(objectList);
    }

    @Override
    public User1C read(long id) {
        return user1CRepository.getOne(id);
    }

    @Override
    public void delete(User1C object) {
        user1CRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        user1CRepository.deleteById(id);
    }
}