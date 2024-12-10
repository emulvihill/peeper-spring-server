package com.snazzyrobot.peeper.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedInput {
    private Long userId;
    private String name;
}