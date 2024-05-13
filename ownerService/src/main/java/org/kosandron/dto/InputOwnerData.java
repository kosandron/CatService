package org.kosandron.dto;

import java.time.LocalDate;

public record InputOwnerData(String name,
                             LocalDate birthDate,
                             String login,
                             String password,
                             String roles
                             ) {}
