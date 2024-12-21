package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.model.ChatResponse;

public interface VisionService {
    ChatResponse compareImages(String before, String after);
}
