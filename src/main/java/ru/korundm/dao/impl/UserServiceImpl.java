package ru.korundm.dao.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.UserService;
import ru.korundm.entity.*;
import ru.korundm.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, User_.LAST_NAME));
    }

    @Override
    public List<User> getAllById(List<Long> idList) {
        return repository.findAllById(idList);
    }

    @Override
    public User save(User object) {
        return repository.save(object);
    }

    @Override
    public List<User> saveAll(List<User> objectList) {
        return repository.saveAll(objectList);
    }

    @Override
    public User read(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(User object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public User findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    @Override
    public List<User> getActiveAll() {
        return repository.findByActiveTrueOrderByUserNameAsc();
    }

    @Override
    public List<User> getByLastNameIn(List<String> lastNameList) {
        return repository.findByLastNameInOrderByLastName(lastNameList);
    }

    @Override
    public boolean hasPrivilege(String userName, String key) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        ListJoin<User, Privilege> privilegeListJoin = root.join(User_.privilegeList, JoinType.LEFT);
        ListJoin<Role, Privilege> rolePrivilegeListJoin = root.join(User_.roleList, JoinType.LEFT).join(Role_.privilegeList, JoinType.LEFT);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(User_.userName), userName));
        predicateList.add(cb.or(
            cb.equal(privilegeListJoin.get(Privilege_.key), key),
            cb.equal(rolePrivilegeListJoin.get(Privilege_.key), key)
        ));
        cq.select(cb.count(root)).where(predicateList.toArray(Predicate[]::new));
        return entityManager.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && repository.existsById(id);
    }

    @Override
    public User findByFirstNameAndLastName(String lastName, String firstName) {
        List<User> userList = repository.findAllByLastNameAndFirstNameOrderByActiveDesc(lastName, firstName);
        return !userList.isEmpty() ? userList.get(0) : null;
    }
}