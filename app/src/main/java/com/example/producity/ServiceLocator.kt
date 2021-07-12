package com.example.producity

import androidx.annotation.VisibleForTesting
import com.example.producity.models.source.*
import com.example.producity.models.source.remote.ActivityRemoteDataSource
import com.example.producity.models.source.remote.ParticipantRemoteDataSource
import com.example.producity.models.source.remote.UserRemoteDataSource

object ServiceLocator {

    @Volatile
    var authRepository: IAuthRepository? = null
        @VisibleForTesting set

    @Volatile
    var userRepository: IUserRepository? = null
        @VisibleForTesting set

    @Volatile
    var activityRepository: IActivityRepository? = null
    @VisibleForTesting set

    @Volatile
    var participantRepository: IParticipantRepository? = null
        @VisibleForTesting set

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

    fun provideActivityRepository(): IActivityRepository {
        synchronized(this) {
            return activityRepository ?: ActivityRepository(ActivityRemoteDataSource())
        }
    }

    fun provideParticipantRepository(): IParticipantRepository {
        synchronized(this) {
            return participantRepository ?: ParticipantRepository(ParticipantRemoteDataSource())
        }
    }

    @VisibleForTesting
    fun resetAllRepositories() {
        synchronized(Any()) {
            // Clear all data to avoid test pollution.
            authRepository = null
            userRepository = null
            participantRepository = null
            activityRepository = null
        }
    }

}