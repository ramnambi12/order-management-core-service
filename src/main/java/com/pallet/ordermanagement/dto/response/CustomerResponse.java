package com.pallet.ordermanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;
}