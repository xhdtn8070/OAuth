package org.tikim.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tikim.boot.domain.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
