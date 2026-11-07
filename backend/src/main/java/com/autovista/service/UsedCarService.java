package com.autovista.service;

import com.autovista.dto.response.UsedCarResponse;
import com.autovista.entity.UsedCar;
import com.autovista.entity.UsedCarStatus;
import com.autovista.exception.ResourceNotFoundException;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.UsedCarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsedCarService {

    private final UsedCarRepository usedCarRepository;
    private final CarMapper carMapper;

    public UsedCarService(UsedCarRepository usedCarRepository, CarMapper carMapper) {
        this.usedCarRepository = usedCarRepository;
        this.carMapper = carMapper;
    }

    @Transactional(readOnly = true)
    public List<UsedCarResponse> getAllUsedCars() {
        return usedCarRepository.findAll().stream()
                .map(carMapper::toUsedCarResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsedCarResponse> getUsedCarsByStatus(String statusStr) {
        if (statusStr == null || statusStr.equalsIgnoreCase("ALL")) {
            return getAllUsedCars();
        }
        try {
            UsedCarStatus status = UsedCarStatus.valueOf(statusStr.toUpperCase());
            return usedCarRepository.findByStatus(status).stream()
                    .map(carMapper::toUsedCarResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return getAllUsedCars();
        }
    }

    @Transactional(readOnly = true)
    public UsedCarResponse getUsedCarById(Long id) {
        UsedCar car = usedCarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pre-Owned Car", "id", id));
        return carMapper.toUsedCarResponse(car);
    }
}
