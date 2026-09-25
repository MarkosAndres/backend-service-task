package com.marcos.incode_home_task.dto;

import java.util.UUID;

public record BackendResponse(UUID verificationId, String query, SearchResult result)
{
}
