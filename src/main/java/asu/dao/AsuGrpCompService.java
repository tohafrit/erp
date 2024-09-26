package asu.dao;

import asu.entity.AsuGrpComp;
import asu.repository.AsuGrpCompRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsuGrpCompService {

    private final AsuGrpCompRepository asuGrpCompRepository;

    public AsuGrpCompService(AsuGrpCompRepository asuGrpCompRepository) {
        this.asuGrpCompRepository = asuGrpCompRepository;
    }

    public AsuGrpComp read(long id) {
        return asuGrpCompRepository.getOne(id);
    }

    public List<AsuGrpComp> getAll() {
        return asuGrpCompRepository.findAll();
    }

    public AsuGrpComp getByCell(String cell) {
        if (StringUtils.isNumeric(cell) && cell.length() == 6) return asuGrpCompRepository.findFirstByNomGrp(Long.valueOf(StringUtils.substring(cell, 0, 2)));
        return null;
    }

    public List<AsuGrpComp> getAllByIdList(List<Long> idList) {
        return asuGrpCompRepository.findAllById(idList);
    }
}