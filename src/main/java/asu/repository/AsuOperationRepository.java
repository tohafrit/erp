package asu.repository;

import asu.entity.AsuOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsuOperationRepository extends JpaRepository<AsuOperation, Long> {

   List<AsuOperation> findAllByToPSOrderByCodeAsc (boolean toPS);

   List<AsuOperation> findAllByCodeInOrderByCodeAsc(List<String> codeList);
}