package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "services")
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String servicesId;

    @Column(nullable = false, length = 255)
    private String nameServices;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descriptionServices;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceServices;

    @Column(nullable = false)
    private LocalTime duration;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "services")
    public List<SkinTherapist> therapists;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private class SkinTherapist {
    }
}