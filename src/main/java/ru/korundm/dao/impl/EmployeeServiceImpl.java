package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EmployeeService;
import ru.korundm.entity.Employee;
import ru.korundm.entity.User;
import ru.korundm.repository.EmployeeRepository;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getAllById(List<Long> idList) { return employeeRepository.findAllById(idList); }

    @Override
    public Employee save(Employee object) {
        return employeeRepository.save(object);
    }

    @Override
    public List<Employee> saveAll(List<Employee> objectList) { return employeeRepository.saveAll(objectList); }

    @Override
    public Employee read(long id) {
        return employeeRepository.getOne(id);
    }

    @Override
    public void delete(Employee object) {
        employeeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> getAllActive(int status) {
        return employeeRepository.findAllByStatusIsNotNullAndStatusEquals(status);
    }

    @Override
    public List<Employee> getAllActiveByAreaId(int status, long areaId) {
        return employeeRepository.findAllByStatusIsNotNullAndStatusEqualsAndProductionArea_Id(status, areaId);
    }

    @Override
    public Employee getActiveEmployeeByUser(User user, int status) {
        return employeeRepository.findFirstByUserAndStatusIsNotNullAndStatusEquals(user, status);
    }
}