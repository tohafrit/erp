package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.DocTemplateTag;
import ru.korundm.enumeration.DocTemplateTagKey;

public interface DocTemplateTagRepository extends JpaRepository<DocTemplateTag, Long> {

    DocTemplateTag findFirstByKey(DocTemplateTagKey key);
}