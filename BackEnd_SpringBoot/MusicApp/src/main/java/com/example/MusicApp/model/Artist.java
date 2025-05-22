package com.example.MusicApp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ARTIST")
@Getter
@Setter
public class Artist extends User {

}

