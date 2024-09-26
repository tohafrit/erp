package eco.dao;

import eco.entity.EcoLaunch;
import eco.repository.EcoLaunchRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EcoLaunchService {

    private final EcoLaunchRepository ecoLaunchRepository;

    public EcoLaunchService(EcoLaunchRepository ecoLaunchRepository) {
        this.ecoLaunchRepository = ecoLaunchRepository;
    }

    public EcoLaunch read(long id) {
        return ecoLaunchRepository.getOne(id);
    }

    public List<EcoLaunch> getAll() {
        return ecoLaunchRepository.findAll(Sort.by(Sort.Order.desc("year"), Sort.Order.desc("numberInYear")));
    }

    public List<EcoLaunch> getAllById(List<Long> idList) {
        return ecoLaunchRepository.findAllById(idList);
    }

    public List<EcoLaunch> getAllByYearAndNumberInYear(LocalDate year, Long launchId) {
        return ecoLaunchRepository.findAllByYearLessThanEqualAndIdNotOrderByYearDescNumberInYearDesc(year, launchId);
    }

    public List<EcoLaunch> getAllByProductId(Long productId) {
        return ecoLaunchRepository.findAllByBomAttributeList_Bom_Product_IdOrderByYearDescNumberInYearDesc(productId);
    }
}