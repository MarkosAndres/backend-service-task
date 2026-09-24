package com.marcos.incode_home_task.dto;

import java.time.LocalDate;

public record CompanyResponse(String cin, String name, LocalDate registrationDate, String address, boolean isActive)
{
    public static CompanyResponse from(Company company)
    {
        return new CompanyResponse(
                company.cin(),
                company.name(),
                company.registrationDate(),
                company.address(),
                company.active());
    }
}
