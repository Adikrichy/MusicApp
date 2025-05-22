package com.example.MusicApp.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
public class
Customer extends User {
    @Enumerated(EnumType.STRING)
    private CustomerType membership;
}

