package com.ezycollect.demo.repository;

import com.ezycollect.demo.dto.PaymentDTO;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<PaymentDTO, Integer> {
}
