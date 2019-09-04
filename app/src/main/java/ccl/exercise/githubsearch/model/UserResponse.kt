package ccl.exercise.githubsearch.model

import com.google.gson.annotations.SerializedName

data class SearchResponse<T>(
    @SerializedName("total_count")
    val TotalCount: Int,
    @SerializedName("items")
    val item: List<T>
)