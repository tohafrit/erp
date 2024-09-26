package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Employee;
import ru.korundm.entity.User;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByStatusIsNotNullAndStatusEquals(int status);

    List<Employee> findAllByStatusIsNotNullAndStatusEqualsAndProductionArea_Id(int status, long areaId);

    Employee findFirstByUserAndStatusIsNotNullAndStatusEquals(User user, int status);
}