package coderuth.k23.skincare_booking.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;


@Entity
@Data
public class CenterSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isClosed;
}
