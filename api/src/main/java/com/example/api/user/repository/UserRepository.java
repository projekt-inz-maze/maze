package com.example.api.user.repository;

import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserById(Long id);
    Boolean existsUserByIndexNumber(Integer indexNumber);
    List<User> findAllByAccountTypeEquals(AccountType accountType);
    Boolean existsByEmail(String email);
}
