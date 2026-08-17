package org.stringtecnologia.string_api.config.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(

        String endpoint,

        String bucket,

        String accessKey,

        String secretKey

) {
}
