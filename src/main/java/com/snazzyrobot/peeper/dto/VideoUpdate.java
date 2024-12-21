package com.snazzyrobot.peeper.dto;

import com.snazzyrobot.peeper.entity.VideoSnap;

import java.util.List;

public record VideoUpdate(VideoSnap current, VideoSnap previous, List<String> comparison) {
}
