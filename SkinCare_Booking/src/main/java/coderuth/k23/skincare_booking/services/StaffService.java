package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.response.StaffInfoResponse;
import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    // Lấy thông tin cơ bản của tất cả nhân viên
    public List<StaffInfoResponse> getAllStaff() {
        List<Staff> staff = staffRepository.findAll();
        if (staff.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu không có dữ liệu
        }
        return staff.stream()
                .map(s -> new StaffInfoResponse(
                        s.getId(),
                        s.getUsername() != null ? s.getUsername() : "",
                        s.getEmail() != null ? s.getEmail() : "",
                        s.getPhone() != null ? s.getPhone() : ""))
                .collect(Collectors.toList());
    }
}
