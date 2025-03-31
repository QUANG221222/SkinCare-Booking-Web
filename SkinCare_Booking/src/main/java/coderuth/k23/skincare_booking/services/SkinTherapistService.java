package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.response.SkinTherapistInfoResponse;
import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SkinTherapistService {

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    // Lấy thông tin cơ bản của tất cả SkinTherapist
    public List<SkinTherapistInfoResponse> getAllSkinTherapists() {
        List<SkinTherapist> therapists = skinTherapistRepository.findAll();
        if (therapists.isEmpty()) {
            return Collections.emptyList();
        }
        return therapists.stream()
                .map(t -> new SkinTherapistInfoResponse(
                        t.getId(),
                        Objects.requireNonNullElse(t.getUsername(), ""),
                        Objects.requireNonNullElse(t.getEmail(), ""),
                        Objects.requireNonNullElse(t.getPhone(), "")))
                .collect(Collectors.toList());
    }
}
