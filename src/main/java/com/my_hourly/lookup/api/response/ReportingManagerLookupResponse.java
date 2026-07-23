package com.my_hourly.lookup.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingManagerLookupResponse {

    private Long id;

    private String employeeCode;

    private String name;

}
