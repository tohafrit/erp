package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.IndexForecastService;
import ru.korundm.entity.IndexForecast;
import ru.korundm.entity.IndexForecast_;
import ru.korundm.entity.Justification;
import ru.korundm.repository.IndexForecastRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Transactional
public class IndexForecastServiceImpl implements IndexForecastService {

    @PersistenceContext
    private EntityManager em;

    private final IndexForecastRepository indexForecastRepository;

    public IndexForecastServiceImpl(IndexForecastRepository indexForecastRepository) {
        this.indexForecastRepository = indexForecastRepository;
    }

    @Override
    public List<IndexForecast> getAll() {
        return indexForecastRepository.findAll();
    }

    @Override
    public List<IndexForecast> getAllById(List<Long> idList) {
        return indexForecastRepository.findAllById(idList);
    }

    @Override
    public IndexForecast save(IndexForecast object) {
        return indexForecastRepository.save(object);
    }

    @Override
    public List<IndexForecast> saveAll(List<IndexForecast> objectList) {
        return indexForecastRepository.saveAll(objectList);
    }

    @Override
    public IndexForecast read(long id) {
        return indexForecastRepository.getOne(id);
    }

    @Override
    public void delete(IndexForecast object) {
        indexForecastRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        indexForecastRepository.deleteById(id);
    }

    /**
     * Метод для получения списка индексов
     * @param justification обоснование
     * @param subclass      тип сущности
     * @return список индексов
     */
    @Override
    public List<IndexForecast> getAllByParams(Justification justification, Class subclass) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<IndexForecast> criteria = builder.createQuery(IndexForecast.class);
        Root<IndexForecast> root = criteria.from(IndexForecast.class);
        criteria.where(builder.and(
            builder.equal(root.get(IndexForecast_.justification), justification),
            builder.equal(root.type(), subclass)
        ));
        criteria.orderBy(builder.asc(root.get(IndexForecast_.year)));

        return em.createQuery(criteria).getResultList();
    }
}