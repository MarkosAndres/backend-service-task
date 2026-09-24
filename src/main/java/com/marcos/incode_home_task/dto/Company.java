package com.marcos.incode_home_task.dto;

import java.time.LocalDate;

public record Company(String cin, String name, LocalDate registrationDate, String address, boolean active)
{
}
