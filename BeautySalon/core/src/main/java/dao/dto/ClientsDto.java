package dao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
//import jakarta.validation.constraints.NotNull;

@Data
public class ClientsDto {
    @NotNull
    private Long id;
    @NotNull
    private String firstName;
    private String lastName;
    private String contact;
    private String dob;
}
