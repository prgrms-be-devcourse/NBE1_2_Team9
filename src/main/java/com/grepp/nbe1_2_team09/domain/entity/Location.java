package com.grepp.nbe1_2_team09.domain.entity;

import com.grepp.nbe1_2_team09.domain.entity.event.EventLocation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location_tb")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false, length = 100)
    private String placeName;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    private String address;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    private String photo;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EventLocation> eventLocation;

    @OneToMany(mappedBy = "startLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Route> routesFrom = new ArrayList<>();

    @OneToMany(mappedBy = "endLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Route> routesTo = new ArrayList<>();

    //비즈니스 메서드

    @Builder
    public Location(String placeName, BigDecimal latitude, BigDecimal longitude, String address, BigDecimal rating, LocationType type,String photo, EventLocation eventLocation) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.rating = rating;
        this.type = type;
        this.photo = photo;

    }

    public void updateLocationDetails(String address, BigDecimal rating) {
        this.address = address;
        this.rating = rating;
    }

    public void updateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateRating(BigDecimal rating) {
        this.rating = rating;
    }
}
