package com.maxime.smul_yas.dto.crm_dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrmResponseDto {
    private String user_id;
    private String firstName;
    private String lastName;
    private String city;
    private String phone;
    private BigDecimal creditBalance;
}
