package com.marcos.incode_home_task.verification;

import com.marcos.incode_home_task.company.Company;

import java.util.List;

public record ThirdPartySearchResult(List<Company> companies, VerificationSource source)
{
}
