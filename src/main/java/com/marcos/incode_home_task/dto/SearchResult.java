package com.marcos.incode_home_task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchResult(String status, CompanyResponse company, List<CompanyResponse> otherResults)
{
    public static SearchResult found(CompanyResponse company, List<CompanyResponse> otherResults)
    {
        return new SearchResult(
                "FOUND",
                company,
                otherResults.isEmpty()
                        ? null
                        : otherResults);
    }

    public static SearchResult noResults()
    {
        return new SearchResult("NO_RESULTS", null, null);
    }
}
