package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.DocTemplate;
import ru.korundm.enumeration.DocTemplateKey;

public interface DocTemplateRepository extends JpaRepository<DocTemplate, Long> {

    DocTemplate findFirstByKey(DocTemplateKey key);
}
