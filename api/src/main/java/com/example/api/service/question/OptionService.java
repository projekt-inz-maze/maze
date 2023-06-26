package com.example.api.service.question;

import com.example.api.model.question.Option;
import com.example.api.repository.question.OptionRepository;
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
