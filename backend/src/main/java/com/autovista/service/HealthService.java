package com.autovista.service;

import com.autovista.dto.response.HealthResponse;
import com.autovista.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@Service
public class HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final ShowroomRepository showroomRepository;
    private final CarModelRepository carModelRepository;
    private final CarVariantRepository carVariantRepository;
    private final UsedCarRepository usedCarRepository;
    private final CityPriceRepository cityPriceRepository;
    private final TestDriveBookingRepository testDriveBookingRepository;
    private final LeadRepository leadRepository;
    private final LeadAssignmentRepository leadAssignmentRepository;

    @Value("${spring.application.name:autovista-backend}")
    private String applicationName;

    public HealthService(DataSource dataSource,
                         UserRepository userRepository,
                         ShowroomRepository showroomRepository,
                         CarModelRepository carModelRepository,
                         CarVariantRepository carVariantRepository,
                         UsedCarRepository usedCarRepository,
                         CityPriceRepository cityPriceRepository,
                         TestDriveBookingRepository testDriveBookingRepository,
                         LeadRepository leadRepository,
                         LeadAssignmentRepository leadAssignmentRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.showroomRepository = showroomRepository;
        this.carModelRepository = carModelRepository;
        this.carVariantRepository = carVariantRepository;
        this.usedCarRepository = usedCarRepository;
        this.cityPriceRepository = cityPriceRepository;
        this.testDriveBookingRepository = testDriveBookingRepository;
        this.leadRepository = leadRepository;
        this.leadAssignmentRepository = leadAssignmentRepository;
    }

    public HealthResponse checkHealth() {
        HealthResponse response = new HealthResponse();
        response.setApplicationName(applicationName);
        response.setEnvironment(System.getProperty("spring.profiles.active", "development"));

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            response.setDatabaseConnected(true);
            response.setDatabaseProductName(metaData.getDatabaseProductName());
            response.setDatabaseVersion(metaData.getDatabaseProductVersion());

            Map<String, Long> tableCounts = new HashMap<>();
            tableCounts.put("users", userRepository.count());
            tableCounts.put("showrooms", showroomRepository.count());
            tableCounts.put("car_models", carModelRepository.count());
            tableCounts.put("car_variants", carVariantRepository.count());
            tableCounts.put("used_cars", usedCarRepository.count());
            tableCounts.put("city_prices", cityPriceRepository.count());
            tableCounts.put("test_drive_bookings", testDriveBookingRepository.count());
            tableCounts.put("leads", leadRepository.count());
            tableCounts.put("lead_assignments", leadAssignmentRepository.count());

            response.setTableCounts(tableCounts);
            response.setStatus("UP");
        } catch (Exception ex) {
            log.error("Database health check failed: ", ex);
            response.setDatabaseConnected(false);
            response.setStatus("DOWN");
            response.setDatabaseProductName("Unknown");
            response.setDatabaseVersion("Unknown");
        }

        return response;
    }
}
