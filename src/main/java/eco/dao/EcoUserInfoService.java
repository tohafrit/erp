package eco.dao;

import eco.entity.EcoUserInfo;
import eco.repository.EcoUserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoUserInfoService {

    private final EcoUserInfoRepository userInfoRepository;

    public EcoUserInfoService(EcoUserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public EcoUserInfo read(long id) {
        return userInfoRepository.getOne(id);
    }

    public List<EcoUserInfo> getAll() {
        return userInfoRepository.findAll();
    }
}