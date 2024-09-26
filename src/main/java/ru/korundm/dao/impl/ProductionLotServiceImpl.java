package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductionLotService;
import ru.korundm.entity.*;
import ru.korundm.repository.ProductionLotRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductionLotServiceImpl implements ProductionLotService {

    @PersistenceContext
    private EntityManager em;

    private ProductionLotRepository productionLotRepository;

    public ProductionLotServiceImpl(ProductionLotRepository productionLotRepository) {
        this.productionLotRepository = productionLotRepository;
    }

    @Override
    public List<ProductionLot> getAll() {
        return productionLotRepository.findAll();
    }

    @Override
    public List<ProductionLot> getAllById(List<Long> idList) {
        return productionLotRepository.findAllById(idList);
    }

    @Override
    public ProductionLot save(ProductionLot object) {
        return productionLotRepository.save(object);
    }

    @Override
    public List<ProductionLot> saveAll(List<ProductionLot> objectList) {
        return productionLotRepository.saveAll(objectList);
    }

    @Override
    public ProductionLot read(long id) {
        return productionLotRepository.getOne(id);
    }

    @Override
    public void delete(ProductionLot object) {
        productionLotRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        productionLotRepository.deleteById(id);
    }

    @Override
    public int getUsageBalanceById(Long productionLotId) {
        String nativeQuery =
            "SELECT\n" +
            "  IFNULL(\n" +
            "    (SELECT\n" +
            "      used_for_sale\n" +
            "    FROM\n" +
            "      v_pl_used_for_sale\n" +
            "    WHERE\n" +
            "      product_lot_id = :productionLotId)\n" +
            "    + \n" +
            "    (SELECT\n" +
            "        used_by_others\n" +
            "    FROM\n" +
            "        v_pl_used_by_others\n" +
            "    WHERE\n" +
            "        product_lot_id = :productionLotId), 0\n" +
            "  )\n" +
            "FROM\n" +
            "  dual";
        Query query = em.createNativeQuery(nativeQuery);
        query.setParameter("productionLotId", productionLotId);
        return ((BigDecimal) query.getSingleResult()).intValue();
    }

    @Override
    public void addProduct(ProductionLot productionLot, int count) {
        productionLot.setAmount(productionLot.getAmount() + count);
        List<ProductionLotSpec> ilist = productionLot.getProductionLotSpecList();
        for (ProductionLotSpec item : ilist) {
            List<ProductionLotSpecProductionLotReference> rlist = item.getProductionLotSpecProductionLotReferenceList();
            ProductionLot tplot = rlist.get(0).getProductionLot();
            int n = count * item.getSubProductAmount().intValue();
            int m = rlist.get(0).getAmount().intValue();
            if ((m + n) > 0) {
                rlist.get(0).setAmount(rlist.get(0).getAmount() + n);
                addProduct(tplot, n);
            } else {
                rlist.get(0).setAmount(0L);
                addProduct(tplot, - m);
                n += m;
                while (n < 0) {
                    if ((rlist.get(1).getAmount() + n) < 0) {
                        n += rlist.get(1).getAmount();
                        rlist.remove(rlist.get(1));
                    } else {
                        rlist.get(1).setAmount(rlist.get(1).getAmount() + n);
                        n = 0;
                    }
                }
            }
        }
    }
}