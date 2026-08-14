package com.malgn.storages.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ToString
@Getter
@Setter
@ConfigurationProperties("storage.aws")
public class AwsStorageProperties {

    private final AwsCdnType type;

    @ConstructorBinding
    public AwsStorageProperties(@DefaultValue("s3_presigned_url") AwsCdnType type) {
        this.type = type;
    }
}
