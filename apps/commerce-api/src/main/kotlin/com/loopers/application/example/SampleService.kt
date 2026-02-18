package com.loopers.application.example

import com.loopers.domain.example.SampleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class SampleService(private val repository: SampleRepository) {
    // 필요한 로직..
}
