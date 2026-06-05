package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.repository.AparelhoRepository;

@Service
@RequiredArgsConstructor
public class AparelhoService {
    private final AparelhoRepository aparelhoRepository;
}
