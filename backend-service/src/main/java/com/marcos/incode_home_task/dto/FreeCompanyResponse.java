package com.marcos.incode_home_task.dto;

import java.time.LocalDate;

/** HTTP representation returned by the independently deployed free provider. */
public record FreeCompanyResponse(
        String cin,
        String name,
        LocalDate registration_date,
        String address,
        boolean is_active)
{
}
