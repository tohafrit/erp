package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

    List<User> findAllByLastNameAndFirstNameOrderByActiveDesc(String lastName, String firstName);

    List<User> findByActiveTrueOrderByUserNameAsc();

    List<User> findByLastNameInOrderByLastName(List<String> lastNameList);

    boolean existsById(long id);
}