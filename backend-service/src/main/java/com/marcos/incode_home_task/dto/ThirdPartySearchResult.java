package com.marcos.incode_home_task.dto;

import java.util.List;

public record ThirdPartySearchResult(List<Company> companies, VerificationSource source)
{
}
