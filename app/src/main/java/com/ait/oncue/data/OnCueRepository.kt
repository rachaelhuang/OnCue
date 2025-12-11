package com.ait.oncue.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// ==========================================
// DATA MODELS
// ==========================================

data class OnCueUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val currentStreak: Int = 0,
    val lastPostDate: Date? = null
)

data class DailyPrompt(
    val id: String = "",
    val text: String = "",
    val subtext: String = "",
    val date: String = "",
    val type: String = "WRITTEN"
) {
    fun getPromptType(): PromptType = try {
        PromptType.valueOf(type)
    } catch (e: IllegalArgumentException) {
        PromptType.WRITTEN
    }
}

enum class PromptType {
    WRITTEN,
    UPLOAD,
    SNAPSHOT
}

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val promptId: String = "",
    val promptText: String = "",
    val promptType: String = "",
    val postDate: String = "", // YYYY-MM-DD - KEY FIELD FOR GROUPING!
    val textContent: String? = null,
    val imageUrl: String? = null,
    val timestamp: Date = Date()
)

// ==========================================
// REPOSITORY
// ==========================================

class OnCueRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // ------------------------------------------
    // AUTHENTICATION
    // ------------------------------------------

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signUp(email: String, pass: String, username: String): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")

            val newUser = OnCueUser(uid = uid, username = username, email = email)
            db.collection("users").document(uid).set(newUser).await()

            Result.success(true)
        } catch (e: Exception) {
            Log.e("OnCueAuth", "Error signing up", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, pass: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("OnCueAuth", "Error signing in", e)
            Result.failure(e)
        }
    }

    // ------------------------------------------
    // PROMPTS
    // ------------------------------------------

    suspend fun getDailyPrompt(dateString: String): Result<DailyPrompt?> {
        return try {
            val snapshot = db.collection("prompts")
                .whereEqualTo("date", dateString)
                .limit(1)
                .get()
                .await()

            val prompt = snapshot.documents.firstOrNull()?.toObject(DailyPrompt::class.java)
            Result.success(prompt)
        } catch (e: Exception) {
            Log.e("OnCueRepo", "Error getting prompt", e)
            Result.failure(e)
        }
    }

    // ------------------------------------------
    // POSTS
    // ------------------------------------------

    suspend fun submitPost(
        promptId: String,
        promptType: String,
        text: String?,
        imageUri: Uri?
    ): Result<Boolean> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))

        return try {
            var downloadUrl: String? = null

            // 1. Upload Image if it exists
            if (imageUri != null) {
                Log.d("OnCueRepo", "Uploading image: $imageUri")
                val ref = storage.reference.child("posts/${user.uid}/${UUID.randomUUID()}.jpg")
                ref.putFile(imageUri).await()
                downloadUrl = ref.downloadUrl.await().toString()
                Log.d("OnCueRepo", "Image uploaded successfully: $downloadUrl")
            }

            // 2. Get current username
            val userDoc = db.collection("users").document(user.uid).get().await()
            val currentUsername = userDoc.getString("username") ?: "Anonymous"

            // 3. Get prompt details
            val promptSnapshot = db.collection("prompts")
                .document(promptId)
                .get()
                .await()

            val promptTypeString = promptSnapshot.getString("type") ?: "WRITTEN"
            val promptTextString = promptSnapshot.getString("text") ?: "Prompt text"
            val promptDateString = promptSnapshot.getString("date") ?: getTodayDateString()

            // 4. Create Post Object with DATE field
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                userId = user.uid,
                username = currentUsername,
                promptId = promptId,
                promptType = promptTypeString,
                promptText = promptTextString,
                postDate = promptDateString, // ← KEY: Store the date!
                textContent = text,
                imageUrl = downloadUrl,
                timestamp = Date()
            )

            // 5. Save to Firestore
            Log.d("OnCueRepo", "Saving post with promptId: $promptId, date: $promptDateString")
            db.collection("posts").document(newPost.id).set(newPost).await()
            Log.d("OnCueRepo", "Post saved successfully!")

            // 6. Update Streak
            updateUserStreak(user.uid)

            Result.success(true)
        } catch (e: Exception) {
            Log.e("OnCuePost", "Error submitting post", e)
            Result.failure(e)
        }
    }

    // ------------------------------------------
    // FEED (REVEAL MECHANIC) - BY DATE NOT PROMPT ID!
    // ------------------------------------------

    sealed class FeedState {
        object Locked : FeedState()
        data class Unlocked(val posts: List<Post>) : FeedState()
        data class Error(val message: String) : FeedState()
    }

    /**
     * Get feed for TODAY'S DATE - includes ALL prompt types!
     */
    suspend fun getFeedForToday(): FeedState {
        val user = auth.currentUser ?: return FeedState.Error("No user logged in")
        val today = getTodayDateString()

        return try {
            Log.d("OnCueFeed", "Loading feed for date: $today")

            // Step 1: Check if CURRENT USER has submitted ANY post today
            val mySubmissionSnapshot = db.collection("posts")
                .whereEqualTo("userId", user.uid)
                .whereEqualTo("postDate", today)
                .limit(1)
                .get()
                .await()

            Log.d("OnCueFeed", "User has submitted today: ${!mySubmissionSnapshot.isEmpty}")

            if (mySubmissionSnapshot.isEmpty) {
                return FeedState.Locked
            }

            // Step 2: Fetch ALL posts for today (any prompt type)
            val allPostsSnapshot = db.collection("posts")
                .whereEqualTo("postDate", today)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("OnCueFeed", "Found ${allPostsSnapshot.documents.size} posts for today")

            val posts = allPostsSnapshot.documents.mapNotNull { doc ->
                try {
                    val post = doc.toObject(Post::class.java)
                    Log.d("OnCueFeed", "Post: ${post?.username} - type: ${post?.promptType}")
                    post
                } catch (e: Exception) {
                    Log.e("OnCueFeed", "Error parsing post", e)
                    null
                }
            }

            FeedState.Unlocked(posts)

        } catch (e: Exception) {
            Log.e("OnCueFeed", "Error getting feed", e)
            FeedState.Error(e.message ?: "Unknown error")
        }
    }

    // ------------------------------------------
    // PROFILE & HISTORY
    // ------------------------------------------

    suspend fun getUserProfile(userId: String): Result<OnCueUser?> {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            val user = snapshot.toObject(OnCueUser::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("OnCueRepo", "Error getting profile", e)
            Result.failure(e)
        }
    }

    suspend fun getUserHistory(userId: String): Result<List<Post>> {
        return try {
            val snapshot = db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                try {
                    Post(
                        id = doc.getString("id") ?: "",
                        userId = doc.getString("userId") ?: "",
                        username = doc.getString("username") ?: "",
                        promptId = doc.getString("promptId") ?: "",
                        promptType = doc.getString("promptType") ?: "",
                        promptText = doc.getString("promptText") ?: "",
                        postDate = doc.getString("postDate") ?: "",
                        textContent = doc.getString("textContent"),
                        imageUrl = doc.getString("imageUrl"),
                        timestamp = doc.getTimestamp("timestamp")?.toDate() ?: Date(0)
                    )
                } catch (e: Exception) {
                    Log.e("OnCueRepository", "Failed to parse post", e)
                    null
                }
            }
            Result.success(posts)
        } catch (e: Exception) {
            Log.e("OnCueRepository", "Failed to fetch user history", e)
            Result.failure(e)
        }
    }

    // ------------------------------------------
    // HELPER FUNCTIONS
    // ------------------------------------------

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private suspend fun updateUserStreak(userId: String) {
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val lastDate = snapshot.getDate("lastPostDate")
            var currentStreak = snapshot.getLong("currentStreak")?.toInt() ?: 0

            val now = Calendar.getInstance()
            val lastPost = Calendar.getInstance()

            if (lastDate != null) {
                lastPost.time = lastDate

                val yesterday = Calendar.getInstance()
                yesterday.add(Calendar.DAY_OF_YEAR, -1)

                val isSameDay = now.get(Calendar.YEAR) == lastPost.get(Calendar.YEAR) &&
                        now.get(Calendar.DAY_OF_YEAR) == lastPost.get(Calendar.DAY_OF_YEAR)

                val isYesterday = yesterday.get(Calendar.YEAR) == lastPost.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == lastPost.get(Calendar.DAY_OF_YEAR)

                if (isYesterday) {
                    currentStreak += 1
                } else if (!isSameDay) {
                    currentStreak = 1
                }
            } else {
                currentStreak = 1
            }

            transaction.update(userRef, "currentStreak", currentStreak)
            transaction.update(userRef, "lastPostDate", now.time)
        }.await()
    }
}