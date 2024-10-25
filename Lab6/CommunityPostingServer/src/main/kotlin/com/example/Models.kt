package com.example


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.sql.Timestamp

object Post: IntIdTable(){
  val postText = varchar("text", 100)
  val timeStamp: Column<Long> = long("timestamp").default(System.currentTimeMillis())
}
