package com.example.producity.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

object EMPTY {
    val banner =
        "https://media4.giphy.com/media/lJWqs22mZzMyv7UUa2/giphy.gif?cid=76400f11w49tz7kdqmd0vgm9uaoboy4nbcisd9s4mum11z3k&rid=giphy.gif&ct=g"
}

@Parcelize
class User(
    val username: String = "",
    val uid: String = "",
    val nickname: String = "",
    var telegramHandle: String = "",
    val gender: Int = 2,
    val birthday: Date = Date(0),
    val bio: String = "",
    val banner: String = EMPTY.banner,
    var rating: Float = 0F,
    var review: Int = 0,
    val latitude: Double = -1.0,
    val longitude: Double = -1.0
) : Parcelable {

    companion object {
        const val MALE = 0
        const val FEMALE = 1
        const val OTHERS = 2
    }

}