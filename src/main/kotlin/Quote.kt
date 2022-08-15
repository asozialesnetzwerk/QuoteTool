import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("author") val author: Author,
    @SerialName("id") val id: Int,
    @SerialName("quote") val quote: String,
)
