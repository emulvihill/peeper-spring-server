package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.Setting;
import com.snazzyrobot.peeper.entity.SettingScope;
import com.snazzyrobot.peeper.repository.SettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    @Cacheable("settings")
    public Optional<String> getSetting(String key) {
        return settingRepository.findByKey(key).map(Setting::getValue);
    }

    @Cacheable("settings")
    public <T> T getSetting(String key, T defaultValue, Function<String, T> converter) {
        return settingRepository.findByKey(key)
                .map(Setting::getValue)
                .map(converter)
                .orElse(defaultValue);
    }

    public String getSetting(String key, String defaultValue) {
        return getSetting(key, defaultValue, Function.identity());
    }

    public Integer getSetting(String key, Integer defaultValue) {
        return getSetting(key, defaultValue, Integer::parseInt);
    }

    public Boolean getSetting(String key, Boolean defaultValue) {
        return getSetting(key, defaultValue, Boolean::parseBoolean);
    }

    @CacheEvict(value = "settings", key = "#key")
    public String saveSetting(String key, String value) {
        Setting setting = settingRepository.findByKey(key)
                .map(existing -> {
                    existing.setValue(value);
                    return existing;
                })
                .orElse(Setting.builder()
                        .key(key)
                        .scope(SettingScope.GLOBAL)
                        .value(value)
                        .build());

        return settingRepository.save(setting).getValue();
    }
}