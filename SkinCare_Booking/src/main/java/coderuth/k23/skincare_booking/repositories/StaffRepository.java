package coderuth.k23.skincare_booking.repositories;

import org.springframework.stereotype.Repository;

import coderuth.k23.skincare_booking.models.Staff;

import java.util.Optional;

@Repository
public interface StaffRepository extends UserBaseRepository<Staff> {
    // This class can be used to add manager-specific queries if needed in the future.
    // Currently, it inherits all methods from UserBaseRepository.

}