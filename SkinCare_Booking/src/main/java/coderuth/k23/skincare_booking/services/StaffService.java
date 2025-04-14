package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.SkinTherapist;
import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getStaffList(){
        return staffRepository.findAll();
    }

    public void deleteStaff(UUID id) {
        staffRepository.deleteById(id);
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
}
