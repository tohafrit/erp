package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.DocTemplateService;
import ru.korundm.entity.DocTemplate;
import ru.korundm.enumeration.DocTemplateKey;
import ru.korundm.repository.DocTemplateRepository;

import java.util.List;

@Service
@Transactional
public class DocTemplateServiceImpl implements DocTemplateService {

    private final DocTemplateRepository docTemplateRepository;

    public DocTemplateServiceImpl(DocTemplateRepository docTemplateRepository) {
        this.docTemplateRepository = docTemplateRepository;
    }

    @Override
    public List<DocTemplate> getAll() {
        return docTemplateRepository.findAll();
    }

    @Override
    public List<DocTemplate> getAllById(List<Long> idList) {
        return docTemplateRepository.findAllById(idList);
    }

    @Override
    public DocTemplate save(DocTemplate object) {
        return docTemplateRepository.save(object);
    }

    @Override
    public List<DocTemplate> saveAll(List<DocTemplate> objectList) {
        return docTemplateRepository.saveAll(objectList);
    }

    @Override
    public DocTemplate read(long id) {
        return docTemplateRepository.getOne(id);
    }

    @Override
    public void delete(DocTemplate object) {
        docTemplateRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        docTemplateRepository.deleteById(id);
    }

    @Override
    public DocTemplate getByKey(DocTemplateKey key) {
        return docTemplateRepository.findFirstByKey(key);
    }
}
