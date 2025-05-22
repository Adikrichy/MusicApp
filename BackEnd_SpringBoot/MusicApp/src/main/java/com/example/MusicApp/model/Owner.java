package com.example.MusicApp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("OWNER")
@Getter
@Setter
public class Owner extends User {

}

