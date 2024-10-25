package edu.msd


import edu.msd.Book.id
import edu.msd.Book.title
import edu.msd.plugins.getSessionData
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.routing

import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureResources() {
    install(Resources)
    routing{
        get<Books> {
            //handler for /books
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Book.slice(Book.title)
                        .selectAll()
                        .map{it[Book.title]}

                }
            )
        }

        get<Books.Liked> {
            val userID = getSessionData(call.sessions).sessionID
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    (Book innerJoin Likes)
                        .slice(Book.title)
                        .select( Likes.user eq userID)
                        .map { it[Book.title] }
                }
            )
        }

        post<Books.Like> {
            val userID = getSessionData(call.sessions).sessionID
            val titleInput = call.receive<BookPostData>().title
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                //returns null, inexplicably!
//                val bookID = Book.insertIgnoreAndGetId{
//                    it[title] = titleInput
//                }!!

                //this is bad because I couldn't figure out non-primary key constraints
                var bookID = Book.select(title eq titleInput).singleOrNull()?.let{ it[Book.id]}
                if(bookID == null) {
                    bookID = Book.insert {
                        it[title] = titleInput
                    }[Book.id]
                }
                Likes.insertIgnore {
                    it[book] = bookID
                    it[user] = userID
                }
            }
            call.respondText("Liked $titleInput")
        }

    }
}


@Serializable data class BookPostData(val title: String)
@Resource("/books") //corresponds to /books
class Books {
    @Resource("liked") //corresponds to /books/liked
    class Liked(val parent: Books = Books())


    @Resource("like") //corresponds to /books/like?title=some%20book
    class Like(val parent: Books = Books(), val title: String? = "")

    @Resource("hate") //corresponds to /books/{id}/hate
    class Hate(val parent: Books = Books(), val title: String? = "")
}
