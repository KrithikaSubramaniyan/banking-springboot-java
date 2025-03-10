package com.github.bank.model.DTO;

import com.github.bank.model.Entity.IndividualUser;
import lombok.Data;

@Data
public class ParentGuardianDTO {
    private Long id;
    private IndividualUser minor;
    private IndividualUser guardian;
}
