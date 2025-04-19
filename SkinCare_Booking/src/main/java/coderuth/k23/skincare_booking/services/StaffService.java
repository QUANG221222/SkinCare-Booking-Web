package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getStaffList(){
        return staffRepository.findAll();
    }

    public void deleteStaff(UUID id) {
        staffRepository.deleteById(id);
    }

    public void updateStaffProfile(String username, EditProfileRequest profileRequest) {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Cập nhật thông tin
        staff.setPhone(profileRequest.getPhone());
        staff.setFullName(profileRequest.getFullName());
        staff.setLocation(profileRequest.getLocation());
        staff.setImg(profileRequest.getImg());

        // Lưu lại thông tin
        staffRepository.save(staff);
    }

    public Staff updateStaff(UUID id, Staff updatedStaff) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found!"));
        staff.setFullName(updatedStaff.getFullName());
        staff.setImg(updatedStaff.getImg());
        staff.setPhone(updatedStaff.getPhone());
        staff.setLocation(updatedStaff.getLocation());
        return staffRepository.save(staff);
    }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, staff.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Cập nhật mật khẩu
        staff.setPassword(passwordEncoder.encode(newPassword));
        staffRepository.save(staff);
    }
}
