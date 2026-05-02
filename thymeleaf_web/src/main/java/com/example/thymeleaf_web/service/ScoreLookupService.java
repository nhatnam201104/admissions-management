package com.example.thymeleaf_web.service;

import com.example.thymeleaf_web.model.dto.ScoreLookupResult;
import com.example.thymeleaf_web.model.entity.Nganh;

import java.util.List;
import java.util.Optional;

public interface ScoreLookupService {
    Optional<ScoreLookupResult> lookupByCccd(String cccd);

    List<Nganh> getAllActiveNganh();
}
