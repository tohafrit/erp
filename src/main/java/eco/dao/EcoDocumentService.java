package eco.dao;

import eco.entity.EcoDocument;
import eco.repository.EcoDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoDocumentService {

    private final EcoDocumentRepository documentRepository;

    public EcoDocumentService(EcoDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public EcoDocument read(long id) {
        return documentRepository.getOne(id);
    }

    public List<EcoDocument> getAll() {
        return documentRepository.findAll();
    }
}