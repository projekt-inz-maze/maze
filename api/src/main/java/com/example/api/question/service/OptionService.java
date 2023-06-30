package com.example.api.question.service;

import com.example.api.question.model.Option;
import com.example.api.question.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OptionService {
    private final OptionRepository optionRepository;

    public Option saveOption(Option option) {
        return optionRepository.save(option);
    }

    public void saveAll(List<Option> options) {
        optionRepository.saveAll(options);
    }
}
