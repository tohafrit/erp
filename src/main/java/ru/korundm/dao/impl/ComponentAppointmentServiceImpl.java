package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ComponentAppointmentService;
import ru.korundm.entity.ComponentAppointment;
import ru.korundm.repository.ComponentAppointmentRepository;

import java.util.List;

@Service
@Transactional
public class ComponentAppointmentServiceImpl implements ComponentAppointmentService {

    private final ComponentAppointmentRepository componentAppointmentRepository;

    public  ComponentAppointmentServiceImpl (ComponentAppointmentRepository componentAppointmentRepository){
        this.componentAppointmentRepository = componentAppointmentRepository;
    }

    @Override
    public List<ComponentAppointment> getAll() {
        return componentAppointmentRepository.findAll();
    }

    @Override
    public List<ComponentAppointment> getAllById(List<Long> idList) {
        return componentAppointmentRepository.findAllById(idList);
    }

    @Override
    public ComponentAppointment save(ComponentAppointment object) {
        return componentAppointmentRepository.save(object);
    }

    @Override
    public List<ComponentAppointment> saveAll(List<ComponentAppointment> objectList) {
        return componentAppointmentRepository.saveAll(objectList);
    }

    @Override
    public ComponentAppointment read(long id) {
        return componentAppointmentRepository.getOne(id);
    }

    @Override
    public void delete(ComponentAppointment object) {
         componentAppointmentRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentAppointmentRepository.deleteById(id);
    }
}