package com.example.api.group.service;

import com.example.api.group.model.AccessDate;
import com.example.api.group.repository.AccessDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccessDateService {
    private final AccessDateRepository accessDateRepository;

    public AccessDate saveAccessDate(AccessDate accessDate){
        return accessDateRepository.save(accessDate);
    }
}
