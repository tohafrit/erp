package ru.korundm.dao;

import ru.korundm.entity.User;

import java.util.List;

public interface UserService extends CommonService<User> {

    User findByUserName(String userName);

    User findByFirstNameAndLastName(String lastName, String firstName);

    List<User> getActiveAll();

    List<User> getByLastNameIn(List<String> lastNameList);

    boolean hasPrivilege(String userName, String key);

    boolean existsById(Long id);
}