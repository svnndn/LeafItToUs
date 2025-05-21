package plant.dto;

import lombok.*;
import plant.model.CareLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlantResponse {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private String type;

    private CareLevel careLevel;

    private String wateringFrequency;

    private String lightingNeeds;

    private String fertilizingFrequency;
}

