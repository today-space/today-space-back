package com.complete.todayspace.global.tracking.service;

import com.complete.todayspace.global.tracking.entity.ExecutionTime;
import com.complete.todayspace.global.tracking.repository.ExecutionTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExecutionTimeService {

    private final ExecutionTimeRepository executionTimeRepository;

    private static final String PACKAGE = "com.complete.todayspace.domain.";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveExecutionTime(String joinPointSignature, Long time) {

        String methodName = joinPointSignature.substring(joinPointSignature.indexOf(" ") + 1)
                .replaceFirst(PACKAGE, "");
        ExecutionTime executionTime = new ExecutionTime(methodName, time);
        executionTimeRepository.save(executionTime);

    }

}
