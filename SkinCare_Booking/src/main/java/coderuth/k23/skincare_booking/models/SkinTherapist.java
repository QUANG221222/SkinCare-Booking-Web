package coderuth.k23.skincare_booking.models;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "skin_therapists")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public class SkinTherapist extends User {


    @Builder.Default
    private String userType = "THERAPIST";

    protected void onCreate() {
        super.onCreate();
        if (getRole() == null) {
            setRole(Role.ROLE_THERAPIST);
        }
    }

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "experience")
    private String experience;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "skinTherapist")
    private List<Feedback> feedbacks; // Danh sách phản hồi liên quan đến chuyên viên

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager; // Quản lý phụ trách chuyên viên
    //Cột manager_id trong bảng skin_therapists sẽ chỉ ra quản lý nào phụ trách chuyên viên đó.

}