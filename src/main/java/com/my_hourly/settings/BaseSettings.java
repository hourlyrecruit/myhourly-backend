package com.my_hourly.settings;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseSettings extends BaseEntity {

    @Column(nullable = false)
    private Boolean active = true;

}
