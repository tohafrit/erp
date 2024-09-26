package ru.korundm.dao;

import ru.korundm.entity.DocTemplateTag;
import ru.korundm.enumeration.DocTemplateTagKey;

public interface DocTemplateTagService extends CommonService<DocTemplateTag> {

    DocTemplateTag getByKey(DocTemplateTagKey key);
}
