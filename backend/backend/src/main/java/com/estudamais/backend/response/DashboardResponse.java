package com.estudamais.backend.response;

import java.util.Map;

public record DashboardResponse(
        Integer totalMinutesStudied,
        Long totalSessionsCount,
        Map<String, Integer> minutesBySubject
) { }