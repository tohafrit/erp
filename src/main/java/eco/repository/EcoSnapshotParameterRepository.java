package eco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import eco.entity.EcoSnapshotParameter;

import java.util.List;

@Repository
public interface EcoSnapshotParameterRepository extends JpaRepository<EcoSnapshotParameter, Long> {

    List<EcoSnapshotParameter> findAllByPurchase_IdAndTypeOrderByIdDesc(long purchaseId, long type);
}