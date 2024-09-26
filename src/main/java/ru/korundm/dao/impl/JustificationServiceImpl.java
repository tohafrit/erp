package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.JustificationService;
import ru.korundm.entity.Justification;
import ru.korundm.entity.Justification_;
import ru.korundm.repository.JustificationRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Transactional
public class JustificationServiceImpl implements JustificationService {

    @PersistenceContext
    private EntityManager em;

    private final JustificationRepository justificationRepository;

    public JustificationServiceImpl(JustificationRepository justificationRepository) {
        this.justificationRepository = justificationRepository;
    }

    @Override
    public List<Justification> getAll() {
        return justificationRepository.findAll();
    }

    @Override
    public List<Justification> getAllById(List<Long> idList) {
        return justificationRepository.findAllById(idList);
    }

    @Override
    public Justification save(Justification object) {
        return justificationRepository.save(object);
    }

    @Override
    public List<Justification> saveAll(List<Justification> objectList) {
        return justificationRepository.saveAll(objectList);
    }

    @Override
    public Justification read(long id) {
        return justificationRepository.getOne(id);
    }

    @Override
    public void delete(Justification object) {
        justificationRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        justificationRepository.deleteById(id);
    }

    /**
     * Метод для получения списка обоснований
     * @param subclass тип сущности (класс)
     * @return список обоснований
     */
    @Override
    public List<Justification> getAllByEntityType(Class subclass) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Justification> criteria = builder.createQuery(Justification.class);
        Root<Justification> root = criteria.from(Justification.class);
        criteria.where(builder.equal(root.type(), subclass))
                .orderBy(builder.desc(root.get(Justification_.id)));

        return em.createQuery(criteria).getResultList();
    }

    /**
     * Метод для получения списка обоснований без текущего обоснования
     * @param subclass          тип сущности (класс)
     * @param justificationId   идентификатор обоснования
     * @return список обоснований
     */
    @Override
    public List<Justification> getAllByEntityTypeAndIdNot(Class subclass, Long justificationId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Justification> criteria = builder.createQuery(Justification.class);
        Root<Justification> root = criteria.from(Justification.class);
        Path<Long> path = root.get(Justification_.id);
        criteria.where(builder.and(builder.equal(root.type(), subclass)), builder.notEqual(path, justificationId))
                .orderBy(builder.desc(path));

        return em.createQuery(criteria).getResultList();
    }
}