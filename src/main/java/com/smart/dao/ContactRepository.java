package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.transaction.Transactional;
import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	//paging
	
	//fing contact by user
	@Query("from Contact c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param ("userId") int userId,Pageable pageable);
	//pageable contains current page-page and no of records per page-6
	
	@Query("select c from Contact c where c.cId=:cId")
	public Contact getContactById(@Param("cId") int cId);
}
