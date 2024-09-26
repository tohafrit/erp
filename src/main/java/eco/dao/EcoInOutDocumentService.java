package eco.dao;

import eco.entity.EcoInOutDocument;
import eco.repository.EcoInOutDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoInOutDocumentService {

    private final EcoInOutDocumentRepository inOutDocumentRepository;

    public EcoInOutDocumentService(EcoInOutDocumentRepository inOutDocumentRepository) {
        this.inOutDocumentRepository = inOutDocumentRepository;
    }

    public EcoInOutDocument read(long id) {
        return inOutDocumentRepository.getOne(id);
    }

    public List<EcoInOutDocument> getAll() {
        return inOutDocumentRepository.findAll();
    }
}