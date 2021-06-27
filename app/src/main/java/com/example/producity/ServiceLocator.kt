package com.example.producity

import androidx.annotation.VisibleForTesting
import com.example.producity.models.source.AuthRepository
import com.example.producity.models.source.IAuthRepository
import com.example.producity.models.source.IUserRepository
import com.example.producity.models.source.UserRepository
import com.example.producity.models.source.remote.UserRemoteDataSource
import com.example.producity.ui.friends.friend_profile.FriendlistOfFriendRepository
import com.example.producity.ui.friends.friend_profile.IFriendlistOfFriendRepository
import com.example.producity.ui.friends.my_friends.FriendListRepository
import com.example.producity.ui.friends.my_friends.IFriendListRepository
import com.example.producity.ui.profile.IProfileRepository
import com.example.producity.ui.profile.ProfileRepository
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    @Volatile
    var profileRepository: IProfileRepository? = null
    @VisibleForTesting set

    @Volatile
    var friendlistOfFriendRepository: IFriendlistOfFriendRepository? = null
    @VisibleForTesting set

    @Volatile
    var friendListRepository: IFriendListRepository? = null
    @VisibleForTesting set

    @Volatile
    var authRepository: IAuthRepository? = null
        @VisibleForTesting set

    @Volatile
    var userRepository: IUserRepository? = null
        @VisibleForTesting set

    fun provideProfileRepository(): IProfileRepository {
        synchronized(this) {
            return profileRepository ?: ProfileRepository()
        }
    }

    fun provideFriendlistOfFriendRepository(): IFriendlistOfFriendRepository {
        synchronized(this) {
            return friendlistOfFriendRepository ?: FriendlistOfFriendRepository()
        }
    }

    fun provideFriendListRepository(): IFriendListRepository {
        synchronized(this) {
            return friendListRepository ?: FriendListRepository()
        }
    }

    fun provideAuthRepository(): IAuthRepository {
        synchronized(this) {
            return authRepository ?: AuthRepository
        }
    }

    fun provideUserRepository(): IUserRepository {
        synchronized(this) {
            return userRepository ?: UserRepository(UserRemoteDataSource())
        }
    }

    @VisibleForTesting
    fun resetAllRepositories() {
        synchronized(Any()) {
            // Clear all data to avoid test pollution.
            profileRepository = null
            friendlistOfFriendRepository = null
            friendListRepository = null
            authRepository = null
            userRepository = null
        }
    }

}