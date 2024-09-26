package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.MessageTemplate;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    MessageTemplate findByType_Code(String code);
}