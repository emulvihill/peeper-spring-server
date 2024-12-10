package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "Feed")
@Table(name = "feed")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;
}
