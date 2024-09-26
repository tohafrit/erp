package asu.repository;

import asu.entity.AsuPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuPostRepository extends JpaRepository<AsuPost, Long> {
}