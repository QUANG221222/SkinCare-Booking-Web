package coderuth.k23.skincare_booking.security;

import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.models.User;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import coderuth.k23.skincare_booking.repositories.ManagerRepository;

import coderuth.k23.skincare_booking.repositories.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private StaffRepository staffRepository;

    // Add other repositories as needed

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find the user in each repository
        Optional<? extends User> user = customerRepository.findByUsername(username);

        if (user.isEmpty()) {
            user = managerRepository.findByUsername(username);
        }

         if (user.isEmpty()) {
             user = staffRepository.findByUsername(username);
         }

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return UserDetailsImpl.build(user.get());
    }

}
