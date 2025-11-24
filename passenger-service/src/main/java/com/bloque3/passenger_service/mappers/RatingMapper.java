package com.bloque3.passenger_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.passenger_service.controllers.dtos.ratingDtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.ratingDtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.ratingDtos.response.RatingResponseDTO;
import com.bloque3.passenger_service.models.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    
    RatingResponseDTO toDto(Rating rating);

    @Mapping(target = "id", ignore = true)
    Rating toEntity(RatingRequestDTO ratingRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromId", ignore = true)
    @Mapping(target = "toId", ignore = true)
    @Mapping(target = "tripId", ignore = true)
    Rating toEntity(RatingRequestUpdateDTO ratingRequestUpdateDTO);
}
