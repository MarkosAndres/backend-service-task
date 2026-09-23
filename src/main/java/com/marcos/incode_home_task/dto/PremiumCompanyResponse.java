package com.marcos.incode_home_task.dto;

import java.time.LocalDate;

public record PremiumCompanyResponse(
        String companyIdentificationNumber,
        String companyName,
        LocalDate registrationDate,
        String companyFullAddress,
        boolean isActive)
{
}
