package com.my_hourly.master.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTitleResponse {

    private Long id;

    private String jobTitleCode;

    private String jobTitle;

    private Long designationId;

    private String designationName;

    private boolean active;

}
