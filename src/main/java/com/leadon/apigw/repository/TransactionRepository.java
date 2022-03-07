package com.leadon.apigw.repository;

import com.leadon.apigw.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

}
