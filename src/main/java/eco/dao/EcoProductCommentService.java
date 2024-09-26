package eco.dao;

import eco.entity.EcoProductComment;
import eco.repository.EcoProductCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductCommentService {

    private final EcoProductCommentRepository ecoProductCommentRepository;

    public EcoProductCommentService(EcoProductCommentRepository ecoProductCommentRepository) {
        this.ecoProductCommentRepository = ecoProductCommentRepository;
    }

    public EcoProductComment read(long id) {
        return ecoProductCommentRepository.getOne(id);
    }

    public List<EcoProductComment> getAll() {
        return ecoProductCommentRepository.findAll();
    }
}