package ru.korundm.dao;

import ru.korundm.entity.Okei;

public interface OkeiService extends CommonService<Okei> {

    Okei getByCode(String code);
}