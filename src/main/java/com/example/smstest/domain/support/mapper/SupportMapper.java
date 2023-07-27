package com.example.smstest.domain.support.mapper;

import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SupportMapper {
    SupportMapper INSTANCE = Mappers.getMapper(SupportMapper.class);

    SupportResponse entityToResponse(Support entity);
}