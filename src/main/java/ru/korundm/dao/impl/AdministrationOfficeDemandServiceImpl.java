package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.AdministrationOfficeDemandService;
import ru.korundm.entity.AdministrationOfficeDemand;
import ru.korundm.entity.AdministrationOfficeDemand_;
import ru.korundm.entity.User;
import ru.korundm.entity.User_;
import ru.korundm.form.search.AdministrationOfficeDemandListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.AdministrationOfficeDemandRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class AdministrationOfficeDemandServiceImpl implements AdministrationOfficeDemandService {

    @PersistenceContext
    private EntityManager em;

    private final AdministrationOfficeDemandRepository administrationOfficeDemandRepository;

    public AdministrationOfficeDemandServiceImpl(AdministrationOfficeDemandRepository administrationOfficeDemandRepository) {
        this.administrationOfficeDemandRepository = administrationOfficeDemandRepository;
    }

    @Override
    public List<AdministrationOfficeDemand> getAll() {
        return administrationOfficeDemandRepository.findAll();
    }

    @Override
    public List<AdministrationOfficeDemand> getAllById(List<Long> idList) {
        return administrationOfficeDemandRepository.findAllById(idList);
    }

    @Override
    public AdministrationOfficeDemand save(AdministrationOfficeDemand object) {
        return administrationOfficeDemandRepository.save(object);
    }

    @Override
    public AdministrationOfficeDemand read(long id) {
        return administrationOfficeDemandRepository.getOne(id);
    }

    @Override
    public void delete(AdministrationOfficeDemand object) {
        administrationOfficeDemandRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        administrationOfficeDemandRepository.deleteById(id);
    }

    @Override
    public List<AdministrationOfficeDemand> saveAll(List<AdministrationOfficeDemand> list) {
        return administrationOfficeDemandRepository.saveAll(list);
    }

    @Override
    public List<AdministrationOfficeDemand> getAllByUser(User user) {
        return administrationOfficeDemandRepository.findAllByUser(user);
    }

    @Override
    public List<AdministrationOfficeDemand> getByTableDataIn(
        TabrIn tableDataIn,
        AdministrationOfficeDemandListFilterForm form
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AdministrationOfficeDemand> criteria = cb.createQuery(AdministrationOfficeDemand.class);
        Root<AdministrationOfficeDemand> root = criteria.from(AdministrationOfficeDemand.class);
        List<Predicate> predicateList = getFormPredicateList(form, root, cb);
        CriteriaQuery<AdministrationOfficeDemand> select = criteria.select(root);
        select.where(predicateList.toArray(new Predicate[0])).distinct(Boolean.TRUE);

        if (!tableDataIn.getSorters().isEmpty()) {
            TabrSorter sorter = tableDataIn.getSorters().get(0);
            List<Path<?>> orderExpressionList = new ArrayList<>();
            switch(sorter.getField()) {
                case "roomNumber":
                    orderExpressionList.add(root.get(AdministrationOfficeDemand_.roomNumber));
                    break;
                case "user":
                    orderExpressionList.add(root.join(AdministrationOfficeDemand_.user).get(User_.lastName));
                    orderExpressionList.add(root.join(AdministrationOfficeDemand_.user).get(User_.firstName));
                    orderExpressionList.add(root.join(AdministrationOfficeDemand_.user).get(User_.middleName));
                    break;
                case "requestDate":
                    orderExpressionList.add(root.get(AdministrationOfficeDemand_.requestDate));
                    break;
                default:
                    orderExpressionList.add(root.get(AdministrationOfficeDemand_.id));
            }
            criteria.orderBy(orderExpressionList.stream()
                .map(ASC.equals(sorter.getDir()) ? cb::asc : cb::desc)
                .collect(Collectors.toList()));
        }
        TypedQuery<AdministrationOfficeDemand> typedQuery = em.createQuery(select);
        typedQuery.setFirstResult(tableDataIn.getStart());
        typedQuery.setMaxResults(tableDataIn.getSize());
        return typedQuery.getResultList();
    }

    @Override
    public long getCountByForm(AdministrationOfficeDemandListFilterForm form) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<AdministrationOfficeDemand> root = criteria.from(AdministrationOfficeDemand.class);
        List<Predicate> predicateList = getFormPredicateList(form, root, cb);
        criteria.select(cb.countDistinct(root)).where(predicateList.toArray(new Predicate[0]));
        return em.createQuery(criteria).getSingleResult();
    }

    private List<Predicate> getFormPredicateList(
        AdministrationOfficeDemandListFilterForm form,
        Root<AdministrationOfficeDemand> root,
        CriteriaBuilder cb
    ) {
        List<Predicate> predicateList = new ArrayList<>();

        String roomNumber = form.getRoomNumber();
        if (StringUtils.isNotBlank(roomNumber)) {
            predicateList.add(cb.like(root.get(AdministrationOfficeDemand_.roomNumber), "%" + roomNumber + "%"));
        }
        String reason = form.getReason();
        if (StringUtils.isNotBlank(reason)) {
            predicateList.add(cb.like(root.get(AdministrationOfficeDemand_.reason), "%" + reason + "%"));
        }

        List<Long> userIdList = form.getUserIdList();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            Join<AdministrationOfficeDemand, User> userJoin = root.join(AdministrationOfficeDemand_.user, JoinType.INNER);
            predicateList.add(cb.and(userJoin.get(User_.id).in(userIdList)));
        }

        Path<LocalDateTime> requestDate = root.get(AdministrationOfficeDemand_.requestDate);
        LocalDateTime requestDateFrom = form.getRequestDateFrom();
        if (requestDateFrom != null) {
            predicateList.add(cb.greaterThanOrEqualTo(requestDate, requestDateFrom));
        }
        LocalDateTime requestDateTo = form.getRequestDateTo();
        if (requestDateTo != null) {
            predicateList.add(cb.lessThanOrEqualTo(requestDate, requestDateTo));
        }
        return predicateList;
    }
}