package com.example.bug_it.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.example.bug_it.BuildConfig
import com.example.bug_it.domain.model.Bug
import com.example.bug_it.domain.model.BugStatus
import com.example.bug_it.domain.repository.BugRepository
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class BugRepositoryImpl @Inject constructor(
    private val sheetsService: Sheets,
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : BugRepository {

    private val dateFormat = SimpleDateFormat("dd-MM-yy")
    private val spreadsheetId = BuildConfig.SPREADSHEET_ID

    override suspend fun getBugs(): Flow<List<Bug>> = flow {
        val range = "${dateFormat.format(Date())}!A:F"
        val response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()

        val bugs = response.getValues()?.map { row ->
            Bug(
                id = row.getOrNull(0)?.toString() ?: "",
                title = row.getOrNull(1)?.toString() ?: "",
                description = row.getOrNull(2)?.toString() ?: "",
                imageUrl = row.getOrNull(3)?.toString() ?: "",
                timestamp = dateFormat.parse(
                    row.getOrNull(4)?.toString() ?: dateFormat.format(Date())
                )?.time ?: System.currentTimeMillis(),
                status = BugStatus.valueOf(row.getOrNull(5)?.toString() ?: BugStatus.OPEN.name)
            )
        } ?: emptyList()

        emit(bugs)
    }.flowOn(Dispatchers.IO)

    override suspend fun uploadImage(imageUri: String): Result<String> = runCatching {
        return try {
            val bucket = supabaseClient.storage["bug"]
            val inputStream = context.contentResolver.openInputStream(imageUri.toUri())
                ?: return Result.failure(IOException("Cannot open file"))

            val bytes = inputStream.use { it.readBytes() }
            val fileName = "bug_images/${UUID.randomUUID()}.jpg"

            bucket.upload(fileName, bytes, upsert = true)
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitBug(bug: Bug): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val today = dateFormat.format(Date())
            val values = listOf(
                listOf(
                    bug.id,
                    bug.title,
                    bug.description,
                    bug.imageUrl,
                    dateFormat.format(Date(bug.timestamp)),
                    bug.status.name
                )
            )

            val body = ValueRange().setValues(values)
            sheetsService.spreadsheets().values()
                .append(spreadsheetId, "$today!A:F", body)
                .setValueInputOption("RAW")
                .execute()
        }
    }
} 