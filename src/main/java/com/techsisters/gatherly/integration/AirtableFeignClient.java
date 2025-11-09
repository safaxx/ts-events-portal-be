package com.techsisters.gatherly.integration;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface AirtableFeignClient {

    @RequestLine("GET /{baseId}/{tableName}")
    @Headers({ "Authorization: Bearer {bearerAuth}" })
    String getList(@Param("bearerAuth") String bearerAuth, @Param("baseId") String baseId,
            @Param("tableName") String tableName);
}
