package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.Manager;
import coderuth.k23.skincare_booking.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void updateManagerProfile(String username, EditProfileRequest profileRequest) {
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found"));

        // Cập nhật thông tin
        manager.setPhone(profileRequest.getPhone());
        manager.setFullName(profileRequest.getFullName());
        manager.setLocation(profileRequest.getLocation());
        manager.setImg(profileRequest.getImg());

        // Lưu lại thông tin
        managerRepository.save(manager);
    }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, manager.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Cập nhật mật khẩu
        manager.setPassword(passwordEncoder.encode(newPassword));
        managerRepository.save(manager);
    }
}
