package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.model.ChatResponse;

import java.io.IOException;

public interface VisionService {
    ChatResponse compareImages(String before, String after) throws IOException;
}
