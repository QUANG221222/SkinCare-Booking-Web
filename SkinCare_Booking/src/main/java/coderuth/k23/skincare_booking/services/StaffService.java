package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.models.Staff;
import coderuth.k23.skincare_booking.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getStaffList(){
        return staffRepository.findAll();
    }

}
