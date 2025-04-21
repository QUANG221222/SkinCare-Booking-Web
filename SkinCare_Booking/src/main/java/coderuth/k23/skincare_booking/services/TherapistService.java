package coderuth.k23.skincare_booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.TherapistSchedule;
import coderuth.k23.skincare_booking.repositories.SkinTherapistRepository;
import coderuth.k23.skincare_booking.repositories.TherapistScheduleRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class TherapistService {

    @Autowired
    private SkinTherapistRepository skinTherapistRepository;

    @Autowired
    private TherapistScheduleRepository therapistScheduleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Quản lý thông tin nhà trị liệu
    public List<SkinTherapist> getAllTherapists() {
        return skinTherapistRepository.findAll();
    }

    public SkinTherapist getTherapistById(UUID id) {
        return skinTherapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found!"));
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TherapistSchedule createSchedule(UUID therapistId, String dayOfWeek, String startTime, String endTime) {
        SkinTherapist therapist = skinTherapistRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found!"));
        LocalTime startTimeParsed;
        LocalTime endTimeParsed;
        try {
            startTimeParsed = LocalTime.parse(startTime);
            endTimeParsed = LocalTime.parse(endTime);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid time format for startTime or endTime. Expected format: HH:mm");
        }

        // Kiểm tra xung đột lịch
        List<TherapistSchedule> existingSchedules = therapistScheduleRepository.findBySkinTherapistId(therapistId);
        for (TherapistSchedule existing : existingSchedules) {
            if (existing.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) { // Không phân biệt hoa thường
                LocalTime existingStart = existing.getStartTime();
                LocalTime existingEnd = existing.getEndTime();

                // Log để kiểm tra giá trị
                System.out.println("Checking conflict: New schedule [" + startTimeParsed + " - " + endTimeParsed + "] vs Existing schedule [" + existingStart + " - " + existingEnd + "]");

                if (existingStart != null && existingEnd != null) {
                    if (!(endTimeParsed.compareTo(existingStart) <= 0 || startTimeParsed.compareTo(existingEnd) >= 0)) {
                        throw new RuntimeException("Schedule conflict on " + dayOfWeek);
                    }
                }
            }
        }

        TherapistSchedule schedule = new TherapistSchedule();
        schedule.setSkinTherapist(therapist);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTimeParsed);
        schedule.setEndTime(endTimeParsed);
        return therapistScheduleRepository.save(schedule);
    }

    public TherapistSchedule updateSchedule(UUID id, String dayOfWeek, String startTime, String endTime) {
        TherapistSchedule schedule = therapistScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found!"));

        UUID therapistId = schedule.getSkinTherapist().getId();

      
        LocalTime startTimeParsed;
        LocalTime endTimeParsed;
        try {
            startTimeParsed = LocalTime.parse(startTime);
            endTimeParsed = LocalTime.parse(endTime);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid time format for startTime or endTime. Expected format: HH:mm");
        }

        // Kiểm tra xung đột lịch
        List<TherapistSchedule> existingSchedules = therapistScheduleRepository.findBySkinTherapistId(therapistId);
        for (TherapistSchedule existing : existingSchedules) {
            if (!existing.getId().equals(id) && existing.getDayOfWeek().equals(dayOfWeek)) {
                if (!(endTimeParsed.compareTo(existing.getStartTime()) <= 0 || startTimeParsed.compareTo(existing.getEndTime()) >= 0)) {
                    throw new RuntimeException("Schedule conflict on " + dayOfWeek);
                }
            }
        }

        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTimeParsed);
        schedule.setEndTime(endTimeParsed);
        return therapistScheduleRepository.save(schedule);
    }


    public void deleteSchedule(UUID id) {
        therapistScheduleRepository.deleteById(id);
    }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        SkinTherapist skinTherapist = skinTherapistRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Skin Therapist not found"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, skinTherapist.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Cập nhật mật khẩu
        skinTherapist.setPassword(passwordEncoder.encode(newPassword));
        skinTherapistRepository.save(skinTherapist);
    }

}