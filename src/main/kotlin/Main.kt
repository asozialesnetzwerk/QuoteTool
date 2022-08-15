import java.net.URL
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val apiRoot = "https://zitate.prapsschnalinen.de/api"
        val authorJson = /*File("/tmp/Download/authors.json").readText() // */ URL("$apiRoot/authors").readText()
        val quoteJson = /* File("/tmp/Download/quotes.json").readText() // */ URL("$apiRoot/quotes").readText()
        
        val authors = Json.decodeFromString(ListSerializer(Author.serializer()), authorJson)
        val quotes = Json.decodeFromString(ListSerializer(Quote.serializer()), quoteJson)
        
        println("Ohne eigenes Zitat")
        
        val orphans = authors.filter { a -> quotes.find { it.author.id == a.id } == null }
        orphans.forEach {
            println(buildString {
                append(it.id.toString().padStart(4))
                append(" ")
                append(it.author)
            })
        }
        println("-------")

//        val filteredAuthors = authors.filterNot { orphans.contains(it) }
        
        println("Doppelte")
        println("Dupl - Orig - Anteil des gemeinsamen Textes")
        for (x in authors) {
            for (y in authors) {
                if (y.id >= x.id) continue
                
                val lcs = lcs(x.author, y.author)
                val percentage = lcs.length.toFloat() / listOf(x.author.length, y.author.length).minBy { it }
                if (percentage > 0.75 && lcs.length > 3) {
                    println(buildString {
                        append(x.id.toString().padStart(4))
                        append(" - ")
                        append(y.id.toString().padStart(4))
                        append(" ")
                        append(percentage.toString().take(3))
                        append(" '")
                        append(x.author)
                        append("' '")
                        append(y.author)
                        append("'")
                    })
                }
            }
        }
    }
}

fun lcs(a: String, b: String): String {
    if (a.length > b.length) return lcs(b, a)
    var res = ""
    for (ai in a.indices) {
        for (len in a.length - ai downTo 1) {
            for (bi in 0 until b.length - len) {
                if (a.regionMatches(ai, b, bi, len, ignoreCase = true) && len > res.length) {
                    res = a.substring(ai, ai + len)
                }
            }
        }
    }
    return res
}
