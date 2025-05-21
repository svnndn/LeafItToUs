package plant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "plants", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    private String type;

    private CareLevel careLevel; // LOW, MEDIUM, HIGH

    private String wateringFrequency;

    private String lightingNeeds;

    private String fertilizingFrequency;

}
