package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.transaction.Transactional;
import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	//paging
	
	//fing contact by user
	@Query("from Contact c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param ("userId") int userId,Pageable pageable);
	//pageable contains current page-page and no of records 
	
	
	public Contact findBycIdAndUser(int cId,User user);
	
	public List<Contact> findByNameContainingAndUser(String keyword,User user );
}
