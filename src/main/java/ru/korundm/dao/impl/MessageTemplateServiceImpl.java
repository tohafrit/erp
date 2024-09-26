package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.MessageTemplateService;
import ru.korundm.entity.MessageTemplate;
import ru.korundm.entity.MessageTemplate_;
import ru.korundm.entity.MessageType_;
import ru.korundm.form.search.MessageTemplateListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.MessageTemplateRepository;

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
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @PersistenceContext
    private EntityManager entityManager;

    private final MessageTemplateRepository messageTemplateRepository;

    public MessageTemplateServiceImpl(MessageTemplateRepository messageTemplateRepository) {
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @Override
    public List<MessageTemplate> getAll() {
        return messageTemplateRepository.findAll();
    }

    @Override
    public List<MessageTemplate> getAllById(List<Long> idList) { return messageTemplateRepository.findAllById(idList); }

    @Override
    public MessageTemplate save(MessageTemplate object) {
        return messageTemplateRepository.save(object);
    }

    @Override
    public List<MessageTemplate> saveAll(List<MessageTemplate> objectList) { return messageTemplateRepository.saveAll(objectList); }

    @Override
    public MessageTemplate read(long id) {
        return messageTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(MessageTemplate object) {
        messageTemplateRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        messageTemplateRepository.deleteById(id);
    }

    @Override
    public MessageTemplate getByCode(String code) {
        return messageTemplateRepository.findByType_Code(code);
    }

    @Override
    public TabrResultQuery<MessageTemplate> queryDataByFilterForm(
        TabrIn tableDataIn,
        MessageTemplateListFilterForm form
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MessageTemplate> cqData = cb.createQuery(MessageTemplate.class);
        Root<MessageTemplate> root = cqData.from(MessageTemplate.class);
        CriteriaQuery<MessageTemplate> selectData = cqData.select(root);
        selectData.where(predicateListByFilterForm(form, root, cb).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = tableDataIn.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            List<Expression<?>> orderExpressionList = new ArrayList<>();
            switch(sorter.getField()) {
                case MessageTemplate_.TYPE:
                    orderExpressionList.add(root.get(MessageTemplate_.type).get(MessageType_.name));
                    break;
                case MessageTemplate_.ACTIVE:
                    orderExpressionList.add(root.get(MessageTemplate_.active));
                    break;
                case MessageTemplate_.SUBJECT:
                    orderExpressionList.add(root.get(MessageTemplate_.subject));
                    break;
                default:
                    orderExpressionList.add(root.get(MessageTemplate_.id));
            }
            cqData.orderBy(orderExpressionList.stream().map(ASC.equals(sorter.getDir()) ? cb::asc : cb::desc).collect(Collectors.toList()));
        }
        TypedQuery<MessageTemplate> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(tableDataIn.getStart());
        tqData.setMaxResults(tableDataIn.getSize());
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<MessageTemplate> rootCount = cCount.from(MessageTemplate.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListByFilterForm(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(MessageTemplateListFilterForm form, Root<MessageTemplate> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        String messageTypeName = form.getMessageTypeName();
        if (StringUtils.isNotBlank(messageTypeName)) {
            predicateList.add(cb.like(root.get(MessageTemplate_.type).get(MessageType_.name), "%" + messageTypeName + "%"));
        }
        if (form.getActive() != null) {
            predicateList.add(cb.equal(root.get(MessageTemplate_.active), form.getActive()));
        }
        String subject = form.getSubject();
        if (StringUtils.isNotBlank(subject)) {
            predicateList.add(cb.like(root.get(MessageTemplate_.subject), "%" + subject + "%"));
        }
        return predicateList;
    }
}
