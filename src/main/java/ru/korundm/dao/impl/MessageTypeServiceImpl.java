package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.MessageTypeService;
import ru.korundm.entity.MessageType;
import ru.korundm.entity.MessageType_;
import ru.korundm.form.search.MessageTypeListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.MessageTypeRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class MessageTypeServiceImpl implements MessageTypeService {

    @PersistenceContext
    private EntityManager entityManager;

    private final MessageTypeRepository messageTypeRepository;

    public MessageTypeServiceImpl(MessageTypeRepository messageTypeRepository) {
        this.messageTypeRepository = messageTypeRepository;
    }

    @Override
    public List<MessageType> getAll() {
        return messageTypeRepository.findAll();
    }

    @Override
    public List<MessageType> getAllById(List<Long> idList) { return messageTypeRepository.findAllById(idList); }

    @Override
    public MessageType save(MessageType object) {
        return messageTypeRepository.save(object);
    }

    @Override
    public List<MessageType> saveAll(List<MessageType> objectList) { return messageTypeRepository.saveAll(objectList); }

    @Override
    public MessageType read(long id) {
        return messageTypeRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(MessageType object) {
        messageTypeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        messageTypeRepository.deleteById(id);
    }

    @Override
    public boolean isUniqueCode(Long id, String code) {
        return id != null ? messageTypeRepository.existsByCodeAndIdNot(code, id) : messageTypeRepository.existsByCode(code);
    }

    @Override
    public TabrResultQuery<MessageType> queryDataByFilterForm(
        TabrIn tableDataIn,
        MessageTypeListFilterForm form
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MessageType> cqData = cb.createQuery(MessageType.class);
        Root<MessageType> root = cqData.from(MessageType.class);
        CriteriaQuery<MessageType> selectData = cqData.select(root);
        selectData.where(predicateListByFilterForm(form, root, cb).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = tableDataIn.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            List<Expression<?>> orderExpressionList = new ArrayList<>();
            switch(sorter.getField()) {
                case MessageType_.NAME:
                    orderExpressionList.add(root.get(MessageType_.name));
                    break;
                case MessageType_.DESCRIPTION:
                    orderExpressionList.add(root.get(MessageType_.description));
                    break;
                case MessageType_.CODE:
                    orderExpressionList.add(root.get(MessageType_.code));
                    break;
                default:
                    orderExpressionList.add(root.get(MessageType_.id));
            }
            cqData.orderBy(orderExpressionList.stream().map(ASC.equals(sorter.getDir()) ? cb::asc : cb::desc).collect(Collectors.toList()));
        }
        TypedQuery<MessageType> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(tableDataIn.getStart());
        tqData.setMaxResults(tableDataIn.getSize());
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<MessageType> rootCount = cCount.from(MessageType.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListByFilterForm(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(MessageTypeListFilterForm form, Root<MessageType> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        String name = form.getName();
        if (StringUtils.isNotBlank(name)) {
            predicateList.add(cb.like(root.get(MessageType_.name), "%" + name + "%"));
        }
        String description = form.getDescription();
        if (StringUtils.isNotBlank(description)) {
            predicateList.add(cb.like(root.get(MessageType_.description), "%" + description + "%"));
        }
        String code = form.getCode();
        if (StringUtils.isNotBlank(code)) {
            predicateList.add(cb.like(root.get(MessageType_.code), "%" + code + "%"));
        }
        return predicateList;
    }
}
