package co.pinkroom.multiplatformtemplate.data.repository

import co.pinkroom.multiplatformtemplate.ApplicationDispatcher
import co.pinkroom.multiplatformtemplate.data.model.Meme
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.URLProtocol
import kotlinx.coroutines.experimental.launch
import kotlinx.serialization.json.JsonTreeParser

class MemeRepository {

    private val client = HttpClient()

    fun getMemes(callback: (List<Meme>) -> Unit) {
        launch(ApplicationDispatcher) {
            val result = getMemesFromServer()
            callback(parseResultToMemes(result))
        }
    }

    private suspend fun getMemesFromServer(): String {
        return client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "48610d5f.ngrok.io"
                port = URLProtocol.HTTPS.defaultPort
                encodedPath = "memes"
            }
        }
    }

    private fun parseResultToMemes(result: String): List<Meme> {
        val elem = JsonTreeParser(result).read()
        return elem.jsonArray.map {
            Meme(it.jsonObject["title"].primitive.content, it.jsonObject["url"].primitive.content)
        }
    }
}