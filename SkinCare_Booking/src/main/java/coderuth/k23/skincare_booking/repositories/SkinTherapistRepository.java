package coderuth.k23.skincare_booking.repositories;
import org.springframework.stereotype.Repository;

import coderuth.k23.skincare_booking.models.SkinTherapist;

@Repository
public interface SkinTherapistRepository extends UserBaseRepository<SkinTherapist> {
    // This class can be used to add manager-specific queries if needed in the future.
    // Currently, it inherits all methods from UserBaseRepository.

}
