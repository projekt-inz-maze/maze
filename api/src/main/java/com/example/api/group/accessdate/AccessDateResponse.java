package com.example.api.group.accessdate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessDateResponse {
    private final Long from;
    private final Long to;
}
