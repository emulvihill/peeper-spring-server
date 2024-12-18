package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.ComparisonResult;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.ComparisonResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ComparisonProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonProcessorService.class);
    private final ComparisonResultRepository comparisonResultRepository;

    public ComparisonProcessorService(ComparisonResultRepository comparisonResultRepository) {
        this.comparisonResultRepository = comparisonResultRepository;
    }

    public List<ComparisonResult> processComparisonResponse(VideoSnap before, VideoSnap after, ChatResponse response) {

        logger.debug(response.toString());

        OffsetDateTime date = OffsetDateTime.now();

        var comparisonResultList = response.getResults().stream().map(result -> {
            var strResult = result.getOutput().getContent();

            logger.info(strResult);
            ComparisonResult comparisonResult = ComparisonResult.builder()
                    .responseId(response.getMetadata().getId())
                    .before(before)
                    .after(after)
                    .date(date)
                    .result(strResult)
                    .resultDetected(strResult.contains("***")).build();
            return comparisonResultRepository.save(comparisonResult);
        }).toList();

        return comparisonResultList;
    }
}
