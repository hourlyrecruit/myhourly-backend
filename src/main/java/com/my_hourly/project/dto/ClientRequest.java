package com.my_hourly.project.dto;

import com.my_hourly.project.entity.ClientStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    private String clientCode;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private ClientStatus status;

}
