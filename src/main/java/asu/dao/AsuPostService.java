package asu.dao;

import asu.entity.AsuPost;
import asu.repository.AsuPostRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuPostService {

    private final AsuPostRepository asuPostRepository;

    public AsuPostService(AsuPostRepository asuPostRepository) {
        this.asuPostRepository = asuPostRepository;
    }

    public AsuPost read(long id) {
        return asuPostRepository.getOne(id);
    }
}