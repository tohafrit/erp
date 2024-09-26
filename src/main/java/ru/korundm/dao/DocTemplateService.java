package ru.korundm.dao;

import ru.korundm.entity.DocTemplate;
import ru.korundm.enumeration.DocTemplateKey;

public interface DocTemplateService extends CommonService<DocTemplate> {

    DocTemplate getByKey(DocTemplateKey key);
}
