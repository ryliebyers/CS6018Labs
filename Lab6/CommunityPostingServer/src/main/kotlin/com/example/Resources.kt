package com.example



import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.delete
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll


fun Application.configureResources() {
    install(Resources)
    routing {
        get<Posts> {
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Post
                        .selectAll()
                        .orderBy(Post.timeStamp, SortOrder.DESC)
                        .map { row ->
                            val timeStamp = row[Post.timeStamp]
                            val text = row[Post.postText]
                            val id = row[Post.id]
                            "id: $id, timestamp: $timeStamp, text: $text"
                        }
                }
            )
        }
        get<Posts.Since> {
            if(it.time == null)
                call.respondText("time Invalid")
            val time: Long = it.time!!
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Post
                        .select( Post.timeStamp.greaterEq(time))
                        .orderBy(Post.timeStamp, SortOrder.DESC)
                        .map { row ->
                            val timeStamp = row[Post.timeStamp]
                            val text = row[Post.postText]
                            val id = row[Post.id]
                            "id: $id, timestamp: $timeStamp, text: $text"
                        }
                }
            )
        }
        get<Posts.Byid> {
            if(it.id == null)
                call.respondText("id Invalid")
            val id: Int = it.id!!
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Post
                        .select( Post.id.eq(id))
                        .orderBy(Post.timeStamp, SortOrder.DESC)
                        .map { row ->
                            val timeStamp = row[Post.timeStamp]
                            val text = row[Post.postText]
                            val myid = row[Post.id]
                            "id: $myid, timestamp: $timeStamp, text: $text"
                        }
                }
            )
        }
        post<Posts.Create> {

            val textInput = call.receive<PostData>().text
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {

                val postID = Post.insert {
                    it[postText] = textInput
                    it[timeStamp] = System.currentTimeMillis()
                }[Post.id]
                call.respondText("Posted:  $postID")
            }
        }
        delete<Posts.Delete> {
            if(it.id == null)
                call.respondText("id Invalid")
            val id: Int = it.id!!
            val deletedRowCount = newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Post.deleteWhere { Post.id eq id }
            }
            if (deletedRowCount > 0) {
                call.respondText("Post with id $id has been deleted.")
            } else {
                call.respondText("No post found with id $id.")
            }
        }
    }
}

@Serializable data class PostData(val text: String)
@Resource("/posts") //corresponds to /posts
class Posts {
    @Resource("create") //corresponds to /posts/create?text=some%20text
    class Create(val parent: Posts = Posts())

    @Resource("since") //corresponds to /posts/create?time=###########
    class Since(val parent: Posts = Posts(), val time: Long?)

    @Resource("byid") //corresponds to /posts/create?id=##
    class Byid(val parent: Posts = Posts(), val id: Int?)

    @Resource("delete") //corresponds to /posts/delete?id=##
    class Delete(val parent: Posts = Posts(), val id: Int?)
}
