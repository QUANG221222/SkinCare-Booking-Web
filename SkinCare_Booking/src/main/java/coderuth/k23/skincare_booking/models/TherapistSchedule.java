package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "therapist_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapistSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TherapistSchedule_id", nullable = false, unique = true)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "skin_therapist_id", referencedColumnName = "user_id")
    private SkinTherapist skinTherapist;
    
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
