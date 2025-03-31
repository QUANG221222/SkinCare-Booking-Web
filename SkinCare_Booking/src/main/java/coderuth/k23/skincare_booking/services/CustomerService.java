package coderuth.k23.skincare_booking.services;

import coderuth.k23.skincare_booking.dtos.response.CustomerInfoResponse;
import coderuth.k23.skincare_booking.models.Customer;
import coderuth.k23.skincare_booking.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Lấy thông tin cơ bản của tất cả khách hàng
    public List<CustomerInfoResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu không có dữ liệu
        }
        return customers.stream()
                .map(customer -> new CustomerInfoResponse(
                        customer.getId(),
                        customer.getUsername() != null ? customer.getUsername() : "",
                        customer.getEmail() != null ? customer.getEmail() : "",
                        customer.getPhone() != null ? customer.getPhone() : ""))
                .collect(Collectors.toList());
    }
}