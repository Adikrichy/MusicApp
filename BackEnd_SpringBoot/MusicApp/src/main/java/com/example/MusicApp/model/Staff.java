package com.example.MusicApp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("STAFF")
@Getter
@Setter
public class Staff extends User {

}

