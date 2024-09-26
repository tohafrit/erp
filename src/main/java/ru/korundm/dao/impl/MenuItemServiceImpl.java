package ru.korundm.dao.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.MenuItemService;
import ru.korundm.entity.Documentation;
import ru.korundm.entity.Documentation_;
import ru.korundm.entity.MenuItem;
import ru.korundm.entity.MenuItemM;
import ru.korundm.enumeration.MenuItemType;
import ru.korundm.repository.MenuItemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    @PersistenceContext
    private EntityManager entityManager;

    private final MenuItemRepository repository;

    public MenuItemServiceImpl(MenuItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MenuItem> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, MenuItemM.SORT).and(Sort.by(Sort.Direction.ASC, MenuItemM.NAME)));
    }

    @Override
    public List<MenuItem> getAllById(List<Long> idList) {
        return repository.findAllById(idList);
    }

    @Override
    public MenuItem save(MenuItem object) {
        return repository.save(object);
    }

    @Override
    public List<MenuItem> saveAll(List<MenuItem> objectList) {
        return repository.saveAll(objectList);
    }

    @Override
    public MenuItem read(long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(MenuItem object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<MenuItem> getAllAlreadyUsedMenuItem(MenuItem currentMenuItem) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MenuItem> criteriaQuery = cb.createQuery(MenuItem.class);
        Root<Documentation> root = criteriaQuery.from(Documentation.class);
        criteriaQuery.select(root.get(Documentation_.menuItem)).distinct(true);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<MenuItem> getAllByParentNullAndType(MenuItemType type) {
        return repository.getAllByParentIsNullAndType(type);
    }
}