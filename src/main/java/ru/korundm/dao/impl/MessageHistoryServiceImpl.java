package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.MessageHistoryService;
import ru.korundm.entity.MessageHistory;
import ru.korundm.entity.MessageHistory_;
import ru.korundm.entity.MessageTemplate_;
import ru.korundm.repository.MessageHistoryRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhestkov_an
 * Date:   19.12.2018
 */
@Service
@Transactional
public class MessageHistoryServiceImpl implements MessageHistoryService {

    @PersistenceContext
    private EntityManager em;

    private final MessageHistoryRepository messageHistoryRepository;

    public MessageHistoryServiceImpl(MessageHistoryRepository messageHistoryRepository) {
        this.messageHistoryRepository = messageHistoryRepository;
    }

    @Override
    public List<MessageHistory> getAll() {
        return messageHistoryRepository.findAll();
    }

    @Override
    public List<MessageHistory> getAllById(List<Long> idList) {
        return messageHistoryRepository.findAllById(idList);
    }

    @Override
    public MessageHistory save(MessageHistory object) {
        return messageHistoryRepository.save(object);
    }

    @Override
    public List<MessageHistory> saveAll(List<MessageHistory> objectList) {
        return messageHistoryRepository.saveAll(objectList);
    }

    @Override
    public MessageHistory read(long id) {
        return messageHistoryRepository.getOne(id);
    }

    @Override
    public void delete(MessageHistory object) {
        messageHistoryRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        messageHistoryRepository.deleteById(id);
    }

    /**
     * Метод для поиска истории сообщений
     * @param type          тип сообщения
     * @param user          пользователь
     * @param dateDeparture дата отправки
     * @return история сообщений
     */
    public List<MessageHistory> searchByParams(Long type, Long user, LocalDate dateDeparture) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MessageHistory> criteria = cb.createQuery(MessageHistory.class);
        Root<MessageHistory> mh = criteria.from(MessageHistory.class);
        List<Predicate> predicateList = new ArrayList<>();
        if (user > 0) {
            predicateList.add(cb.equal(mh.get(MessageHistory_.user), user));
        }
        if (type > 0) {
            predicateList.add(cb.equal(mh.get(MessageHistory_.messageTemplate).get(MessageTemplate_.type), type));
        }
        if (dateDeparture != null) {
            LocalDateTime ldtStart = dateDeparture.atStartOfDay();
            LocalDateTime ldtEnd = dateDeparture.atTime(LocalTime.MAX);
            predicateList.add(cb.between(mh.get(MessageHistory_.departureDate), ldtStart, ldtEnd));
        }
        criteria.select(mh).where(predicateList.toArray(new Predicate[0]));
        return em.createQuery(criteria).getResultList();
    }
}
