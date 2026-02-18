package com.loopers.application.example

import com.loopers.domain.example.Example

data class ExampleInfo(
    val id: Long,
    val name: String,
    val description: String,
) {
    companion object {
        fun from(model: Example): ExampleInfo {
            return ExampleInfo(
                id = model.id,
                name = model.name,
                description = model.description,
            )
        }
    }
}
