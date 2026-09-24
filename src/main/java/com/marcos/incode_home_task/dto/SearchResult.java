package com.marcos.incode_home_task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchResult(String status, CompanyResponse company, List<CompanyResponse> otherResults)
{
    public static SearchResult found(CompanyResponse company, List<CompanyResponse> otherResults)
    {
        var otherCompanies = otherResults.isEmpty()
                ? null
                : otherResults;
        return new SearchResult("FOUND", company, otherCompanies);
    }

    public static SearchResult noResults()
    {
        return new SearchResult("NO_RESULTS", null, null);
    }

    public static SearchResult unavailable()
    {
        return new SearchResult("THIRD_PARTIES_UNAVAILABLE", null, null);
    }
}
