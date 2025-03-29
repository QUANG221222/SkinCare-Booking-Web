package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.SpaServiceRequestDTO;
import coderuth.k23.skincare_booking.dtos.response.SpaServiceResponseDTO;
import coderuth.k23.skincare_booking.models.SpaService;
import coderuth.k23.skincare_booking.repositories.SpaServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpaServiceService {

    @Autowired
    private SpaServiceRepository spaServiceRepository;

    public Optional<SpaServiceResponseDTO> addSpaService(SpaServiceRequestDTO requestDTO) {
        if (spaServiceRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Service name already exists");
        }

        SpaService spaService = new SpaService();
        spaService.setName(requestDTO.getName());
        spaService.setDescription(requestDTO.getDescription());
        spaService.setPrice(requestDTO.getPrice());
        spaService.setDuration(requestDTO.getDuration());

        SpaService savedService = spaServiceRepository.save(spaService);
        return Optional.of(convertToDTO(savedService));
    }

    public Optional<SpaServiceResponseDTO> getSpaServiceById(Long id) {
        return spaServiceRepository.findById(id).map(this::convertToDTO);
    }

    public List<SpaServiceResponseDTO> getAllSpaServices() {
        return spaServiceRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<SpaServiceResponseDTO> updateSpaService(Long id, SpaServiceRequestDTO requestDTO) {
        return spaServiceRepository.findById(id).map(spaService -> {
            spaService.setName(requestDTO.getName());
            spaService.setDescription(requestDTO.getDescription());
            spaService.setPrice(requestDTO.getPrice());
            spaService.setDuration(requestDTO.getDuration());

            SpaService updatedService = spaServiceRepository.save(spaService);
            return convertToDTO(updatedService);
        });
    }

    public boolean deleteSpaService(Long id) {
        if (spaServiceRepository.existsById(id)) {
            spaServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private SpaServiceResponseDTO convertToDTO(SpaService spaService) {
        SpaServiceResponseDTO dto = new SpaServiceResponseDTO();
        dto.setId(spaService.getId());
        dto.setName(spaService.getName());
        dto.setDescription(spaService.getDescription());
        dto.setPrice(spaService.getPrice());
        dto.setDuration(spaService.getDuration());
        return dto;
    }
}
