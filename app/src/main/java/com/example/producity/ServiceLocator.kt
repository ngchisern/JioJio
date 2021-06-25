package com.example.producity

import androidx.annotation.VisibleForTesting
import com.example.producity.ui.friends.friend_profile.FriendlistOfFriendRepository
import com.example.producity.ui.friends.friend_profile.IFriendlistOfFriendRepository
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

    @VisibleForTesting
    fun resetAllRepositories() {
        synchronized(Any()) {
            // Clear all data to avoid test pollution.
            profileRepository = null
            friendlistOfFriendRepository = null
        }
    }

}