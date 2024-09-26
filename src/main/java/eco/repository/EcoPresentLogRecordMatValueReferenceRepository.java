package eco.repository;

import eco.entity.EcoPresentLogRecordMatValueReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoPresentLogRecordMatValueReferenceRepository extends JpaRepository<EcoPresentLogRecordMatValueReference, Long> {
}