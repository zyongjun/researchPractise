package com.example.netmodule;

import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RequestApi {
    @GET
    Response<String> getBadidu(@Url String url);
}
