package com.example.springandthymeleaf.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "thing_location")
public class ThingLocation {

    @Id
    @Column(name = "thing_id")
    private Long thingId;
    @Column(name = "location_id")
    private Long locationId;
}
