package com.my_hourly.lookup.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupResponse {

    private Long id;

    private String name;

}
