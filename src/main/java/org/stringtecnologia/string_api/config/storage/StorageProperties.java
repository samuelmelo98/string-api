package org.stringtecnologia.string_api.config.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.List;

@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {

    private String path;
    private DataSize maxSize;
    private List<String> allowedTypes;
    private List<String> allowedExtensions;
}