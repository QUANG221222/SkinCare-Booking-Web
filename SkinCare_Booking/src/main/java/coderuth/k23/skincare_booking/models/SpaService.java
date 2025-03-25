package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "spa_services")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class SpaService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SpaService_id", nullable = false, unique = true)
    private Long Id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "duration", nullable = false)
    private int duration;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    //Vì Spa là dịch vụ được đặt trong lịch hẹn -> thêm quan hệ @OneToMany với Appointment
    @OneToMany(mappedBy = "spaService")
    private List<Appointment> appointments; // Danh sách lịch hẹn liên quan đến dịch vụ

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    //Vì Spa nhận phản hồi từ khách hàng, bạn nên thêm quan hệ @OneToMany với Feedback
    @OneToMany(mappedBy = "spaService")
    private List<Feedback> feedbacks; // Danh sách phản hồi liên quan đến dịch vụ

}