package com.snazzyrobot.peeper.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoSnapInput {
    private Long feedId;
    private String data;
}