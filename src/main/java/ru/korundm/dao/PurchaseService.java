package ru.korundm.dao;

import ru.korundm.entity.Purchase;

public interface PurchaseService extends CommonService<Purchase> {

    void deleteAll();
}