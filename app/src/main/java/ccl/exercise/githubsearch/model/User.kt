package ccl.exercise.githubsearch.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val userName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("site_admin")
    val isAdmin: Boolean
)