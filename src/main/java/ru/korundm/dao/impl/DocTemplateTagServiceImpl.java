package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.DocTemplateTagService;
import ru.korundm.entity.DocTemplateTag;
import ru.korundm.enumeration.DocTemplateTagKey;
import ru.korundm.repository.DocTemplateTagRepository;

import java.util.List;

@Service
@Transactional
public class DocTemplateTagServiceImpl implements DocTemplateTagService {

    private final DocTemplateTagRepository docTemplateTagRepository;

    public DocTemplateTagServiceImpl(DocTemplateTagRepository docTemplateTagRepository) {
        this.docTemplateTagRepository = docTemplateTagRepository;
    }

    @Override
    public List<DocTemplateTag> getAll() {
        return docTemplateTagRepository.findAll();
    }

    @Override
    public List<DocTemplateTag> getAllById(List<Long> idList) {
        return docTemplateTagRepository.findAllById(idList);
    }

    @Override
    public DocTemplateTag save(DocTemplateTag object) {
        return docTemplateTagRepository.save(object);
    }

    @Override
    public List<DocTemplateTag> saveAll(List<DocTemplateTag> objectList) {
        return docTemplateTagRepository.saveAll(objectList);
    }

    @Override
    public DocTemplateTag read(long id) {
        return docTemplateTagRepository.getOne(id);
    }

    @Override
    public void delete(DocTemplateTag object) {
        docTemplateTagRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        docTemplateTagRepository.deleteById(id);
    }

    @Override
    public DocTemplateTag getByKey(DocTemplateTagKey key) {
        return docTemplateTagRepository.findFirstByKey(key);
    }
}
