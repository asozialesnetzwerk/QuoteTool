import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author(
    @SerialName("author") val author: String,
    @SerialName("id") val id: Int,
)
