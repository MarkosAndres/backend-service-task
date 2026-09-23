package com.marcos.incode_home_task.company;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CompanyService
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Company> find(String resourceName, String query)
    {
        String searchTerm = query.toLowerCase(Locale.ROOT);
        return load(resourceName).stream()
                .filter(company -> company.cin().toLowerCase(Locale.ROOT).contains(searchTerm))
                .toList();
    }

    private List<Company> load(String resourceName)
    {
        ClassPathResource resource = new ClassPathResource(resourceName);
        if (!resource.exists())
        {
            return List.of();
        }

        try (InputStream input = resource.getInputStream())
        {
            JsonNode root = objectMapper.readTree(input);
            JsonNode companies = root.isArray() ? root : root.path("companies");
            List<Company> result = new ArrayList<>();
            for (JsonNode company : companies)
            {
                result.add(new Company(
                        text(company, "cin", "companyIdentificationNumber"),
                        text(company, "name", "companyName"),
                        LocalDate.parse(text(company, "registration_date", "registrationDate")),
                        address(company),
                        company.has("is_active") ? company.path("is_active").asBoolean() : company.path("isActive").asBoolean()));
            }
            return result;
        }
        catch (Exception exception)
        {
            throw new IllegalStateException("Could not read company catalog " + resourceName, exception);
        }
    }

    private String text(JsonNode company, String snakeCaseName, String camelCaseName)
    {
        return company.has(snakeCaseName) ? company.path(snakeCaseName).asText() : company.path(camelCaseName).asText();
    }

    private String address(JsonNode company)
    {
        if (company.has("address"))
        {
            return company.path("address").asText();
        }
        return company.has("companyFullAddress")
                ? company.path("companyFullAddress").asText()
                : company.path("fullAddress").asText();
    }
}
