package com.marcos.incode_home_task.company;

import java.time.LocalDate;

/** The representation used internally, independent of either supplier's API contract. */
public record Company(String cin, String name, LocalDate registrationDate, String address, boolean active)
{
}
