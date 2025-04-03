package coderuth.k23.skincare_booking.repositories;
import coderuth.k23.skincare_booking.models.Customer;
import org.springframework.stereotype.Repository;

import coderuth.k23.skincare_booking.models.SkinTherapist;

import java.util.Optional;

@Repository
public interface SkinTherapistRepository extends UserBaseRepository<SkinTherapist> {
    // This class can be used to add manager-specific queries if needed in the future.
    // Currently, it inherits all methods from UserBaseRepository.

}
