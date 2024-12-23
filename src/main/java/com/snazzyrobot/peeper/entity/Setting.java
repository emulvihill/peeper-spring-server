package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "setting")
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column
    public String key;

    @Column
    @Enumerated(EnumType.STRING)
    public SettingScope scope;

    @Column
    public String value;
}