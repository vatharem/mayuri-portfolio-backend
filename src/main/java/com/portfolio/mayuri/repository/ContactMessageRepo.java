package com.portfolio.mayuri.repository;
import com.portfolio.mayuri.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepo extends JpaRepository<ContactMessage,Long> {

}
