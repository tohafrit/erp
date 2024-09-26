package eco.dao;

import eco.entity.EcoBank;
import eco.repository.EcoBankRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoBankService {

    private final EcoBankRepository ecoBankRepository;

    public EcoBankService(EcoBankRepository ecoBankRepository) {
        this.ecoBankRepository = ecoBankRepository;
    }

    public EcoBank read(long id) {
        return ecoBankRepository.getOne(id);
    }

    public List<EcoBank> getAll() {
        return ecoBankRepository.findAll();
    }
}