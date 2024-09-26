package eco.repository;

import eco.entity.EcoDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoDocumentRepository extends JpaRepository<EcoDocument, Long> {
}
