package ee.piperal.banking_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private long id;
    @NotBlank(message = "Name can't be blank")
    private String username;
    @Email(message = "Email needs to be valid")
    private String email;
    private double balance;
}
