package coderuth.k23.skincare_booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.TherapistSchedule;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TherapistService {

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;

    // Quản lý thông tin nhà trị liệu
    public List<SkinTherapist> getAllTherapists() {
        return skinTherapistRepository.findAll();
    }

    public SkinTherapist createTherapist(SkinTherapist therapist) {
        return skinTherapistRepository.save(therapist);
    }

    public SkinTherapist updateTherapist(UUID id, SkinTherapist updatedTherapist) {
        SkinTherapist therapist = skinTherapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found!"));
        therapist.setUsername(updatedTherapist.getUsername());
        therapist.setFullName(updatedTherapist.getFullName());
        therapist.setImg(updatedTherapist.getImg());
        therapist.setEmail(updatedTherapist.getEmail());
        therapist.setPhone(updatedTherapist.getPhone());
        therapist.setLocation(updatedTherapist.getLocation());
        therapist.setSpecialty(updatedTherapist.getSpecialty());
        return skinTherapistRepository.save(therapist);
    }

    public void deleteTherapist(UUID id) {
        skinTherapistRepository.deleteById(id);
    }

    // Quản lý lịch làm việc của nhà trị liệu
    public List<TherapistSchedule> getSchedulesByTherapist(UUID therapistId) {
        return therapistScheduleRepository.findBySkinTherapistId(therapistId);
    }

    public TherapistSchedule createSchedule(TherapistSchedule schedule) {
        return therapistScheduleRepository.save(schedule);
    }

    public TherapistSchedule updateSchedule(UUID id, TherapistSchedule updatedSchedule) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found!"));
        schedule.setDayOfWeek(updatedSchedule.getDayOfWeek());
        schedule.setStartTime(updatedSchedule.getStartTime());
        schedule.setEndTime(updatedSchedule.getEndTime());
        return therapistScheduleRepository.save(schedule);
    }

    public void deleteSchedule(UUID id) {
        therapistScheduleRepository.deleteById(id);
    }
}