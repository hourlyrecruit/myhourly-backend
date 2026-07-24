package com.my_hourly.lookup.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeIdName {

    private Long id;

    private String employeeName;

}
