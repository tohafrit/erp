package ru.korundm.dao;

import ru.korundm.entity.Justification;

import java.util.List;

public interface JustificationService extends CommonService<Justification> {

    List<Justification> getAllByEntityType(Class subclass);

    List<Justification> getAllByEntityTypeAndIdNot(Class subclass, Long justificationId);
}