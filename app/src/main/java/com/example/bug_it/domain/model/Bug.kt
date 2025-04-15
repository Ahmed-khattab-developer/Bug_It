package com.example.bug_it.domain.model

data class Bug(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: BugStatus = BugStatus.OPEN
)

enum class BugStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED
} 