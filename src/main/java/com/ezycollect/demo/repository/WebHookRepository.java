package com.ezycollect.demo.repository;

import com.ezycollect.demo.dto.WebHookDTO;
import org.springframework.data.repository.CrudRepository;

public interface WebHookRepository extends CrudRepository<WebHookDTO, String> {
}
