package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.request.EditProfileRequest;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy thông tin cơ bản của tất cả khách hàng
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void updateCustomerProfile(String username, EditProfileRequest profileRequest) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        // Cập nhật thông tin
        customer.setPhone(profileRequest.getPhone());
        customer.setFullName(profileRequest.getFullName());
        customer.setLocation(profileRequest.getLocation());

        // Lưu lại thông tin
        customerRepository.save(customer);
    }

    public Customer updateCustomer(UUID id, Customer updatedCustomer) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        customer.setFullName(updatedCustomer.getFullName());
        customer.setPhone(updatedCustomer.getPhone());
        customer.setLocation(updatedCustomer.getLocation());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Cập nhật mật khẩu
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }
}