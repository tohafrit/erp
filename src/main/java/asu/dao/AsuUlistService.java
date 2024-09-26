package asu.dao;

import asu.repository.AsuUlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuUlistService {

    @Autowired
    private AsuUlistRepository asuUlistRepository;
}
