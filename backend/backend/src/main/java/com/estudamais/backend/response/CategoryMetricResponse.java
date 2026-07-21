package com.estudamais.backend.response;

public record CategoryMetricResponse(String category,long total,long completed,int percentage) {
}
