package asu.dao;

import asu.entity.AsuOperation;
import asu.repository.AsuOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsuOperationService {

    @Autowired
    private AsuOperationRepository asuOperationRepository;

    public List<AsuOperation> getAllByToPS(boolean toPS) {
        return asuOperationRepository.findAllByToPSOrderByCodeAsc(toPS);
    }

    public List<AsuOperation> getAllByCode(List<String> codeList) {
        return asuOperationRepository.findAllByCodeInOrderByCodeAsc(codeList);
    }
}