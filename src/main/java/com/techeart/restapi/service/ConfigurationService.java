package com.techeart.restapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService
{
    @Value("${users.minAge}")
    private int userMinAge;
    public int getUserMinAge() { return userMinAge; }

    @Value("${app.paginationDefaultOffset}")
    private int paginationDefaultOffset;
    public int getPaginationDefaultOffset() { return paginationDefaultOffset; }

    @Value("${app.paginationDefaultLimit}")
    private int paginationDefaultLimit;
    public int getPaginationDefaultLimit() { return paginationDefaultLimit; }

    @Value("${app.paginationMaxLimit}")
    private int paginationMaxLimit;
    public int getPaginationMaxLimit() { return paginationMaxLimit; }
}
