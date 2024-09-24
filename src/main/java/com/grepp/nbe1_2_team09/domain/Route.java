package com.grepp.nbe1_2_team09.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_tb")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    @Column(precision = 10, scale = 2)
    private BigDecimal distance;

    private LocalDateTime estimatedTime;
}

