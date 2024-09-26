package eco.dao;

import eco.entity.Eco1CUser;
import eco.repository.Eco1CUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Eco1CUserService {

    private final Eco1CUserRepository eco1CUserRepository;

    public Eco1CUserService(Eco1CUserRepository eco1CUserRepository) {
        this.eco1CUserRepository = eco1CUserRepository;
    }

    public Eco1CUser read(long id) {
        return eco1CUserRepository.getOne(id);
    }

    public List<Eco1CUser> getAll() {
        return eco1CUserRepository.findAll();
    }
}