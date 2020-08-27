package org.tikim.boot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.tikim.boot.domain.entity.DisposableSalt;

public interface DisposableSaltRepository extends JpaRepository<DisposableSalt,Long> {
}
