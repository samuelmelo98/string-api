package org.stringtecnologia.string_api.model.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.model.entities.Arquivo;

import java.util.UUID;

public interface StorageService {

    Arquivo upload(MultipartFile file, String prefixo);
    Resource download( UUID uuid);
    void delete( UUID uuid);

}

