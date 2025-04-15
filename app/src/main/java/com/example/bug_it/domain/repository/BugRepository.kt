package com.example.bug_it.domain.repository

import com.example.bug_it.domain.model.Bug
import kotlinx.coroutines.flow.Flow

interface BugRepository {
    suspend fun getBugs(): Flow<List<Bug>>
    suspend fun uploadImage(imageUri: String): Result<String>
    suspend fun submitBug(bug: Bug): Result<Unit>
} 