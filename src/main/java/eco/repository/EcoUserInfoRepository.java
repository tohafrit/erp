package eco.repository;

import eco.entity.EcoUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoUserInfoRepository extends JpaRepository<EcoUserInfo, Long> {
}
