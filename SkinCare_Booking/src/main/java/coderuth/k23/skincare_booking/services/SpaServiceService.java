package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.SpaServiceDTO;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpaServiceService {
    private final SpaServiceRepository spaServiceRepository;

    @Autowired
    public SpaServiceService(SpaServiceRepository spaServiceRepository) {
        this.spaServiceRepository = spaServiceRepository;
    }

    // Create a new spa service
    @Transactional
    public SpaServiceDTO createSpaService(SpaServiceDTO spaServiceDTO) {
        SpaService spaService = new SpaService();
        BeanUtils.copyProperties(spaServiceDTO, spaService);
        SpaService savedService = spaServiceRepository.save(spaService);

        SpaServiceDTO returnDTO = new SpaServiceDTO();
        BeanUtils.copyProperties(savedService, returnDTO);
        return returnDTO;
    }

    // Get all spa services
    public List<SpaServiceDTO> getAllSpaServices() {
        return spaServiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get spa service by ID
    public SpaServiceDTO getSpaServiceById(Long id) {
        SpaService spaService = spaServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spa service not found with id: " + id));

        SpaServiceDTO dto = convertToDTO(spaService);
        // Additional information
        dto.setId(spaService.getId());
        return dto;
    }

    // Update an existing spa service
    @Transactional
    public SpaServiceDTO updateSpaService(Long id, SpaServiceDTO spaServiceDTO) {
        SpaService existingService = spaServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spa service not found with id: " + id));

        BeanUtils.copyProperties(spaServiceDTO, existingService, "id");
        SpaService updatedService = spaServiceRepository.save(existingService);

        return convertToDTO(updatedService);
    }

    // Delete a spa service
    @Transactional
    public void deleteSpaService(Long id) {
        SpaService spaService = spaServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spa service not found with id: " + id));

        spaServiceRepository.delete(spaService);
    }

    // Search services by name
    public List<SpaServiceDTO> searchServicesByName(String name) {
        return spaServiceRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Find services by price range
    public List<SpaServiceDTO> findServicesByPriceRange(double minPrice, double maxPrice) {
        return spaServiceRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get service statistics
    public ServiceStatisticsDTO getServiceStatistics(Long serviceId) {
        SpaService spaService = spaServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Spa service not found with id: " + serviceId));

        ServiceStatisticsDTO statisticsDTO = new ServiceStatisticsDTO();
        statisticsDTO.setServiceId(serviceId);
        statisticsDTO.setServiceName(spaService.getName());
        statisticsDTO.setTotalAppointments(
                spaServiceRepository.countAppointmentsByServiceId(serviceId)
        );
        statisticsDTO.setAverageRating(
                spaServiceRepository.calculateAverageRatingByServiceId(serviceId)
        );

        return statisticsDTO;
    }

    // Utility method to convert Entity to DTO
    private SpaServiceDTO convertToDTO(SpaService spaService) {
        SpaServiceDTO dto = new SpaServiceDTO();
        BeanUtils.copyProperties(spaService, dto);
        return dto;
    }

    // Inner class for service statistics
    @lombok.Data
    public static class ServiceStatisticsDTO {
        private Long serviceId;
        private String serviceName;
        private int totalAppointments;
        private Double averageRating;
    }
}