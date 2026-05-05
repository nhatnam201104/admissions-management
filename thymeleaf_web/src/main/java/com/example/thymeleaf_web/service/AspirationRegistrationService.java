package com.example.thymeleaf_web.service;

import com.example.thymeleaf_web.model.dto.AspirationRegistrationForm;
import com.example.thymeleaf_web.model.dto.CandidateInfoDto;
import com.example.thymeleaf_web.model.entity.Nganh;

import java.util.List;
import java.util.Optional;

public interface AspirationRegistrationService {

    Optional<CandidateInfoDto> findCandidate(String cccd);

    AspirationRegistrationForm buildRegistrationForm(String cccd);

    List<Nganh> getActiveMajors();

    void register(AspirationRegistrationForm form);
}
