package com.example.producity

import androidx.annotation.VisibleForTesting
import com.example.producity.models.source.AuthRepository
import com.example.producity.models.source.IAuthRepository
import com.example.producity.models.source.IUserRepository
import com.example.producity.models.source.UserRepository
import com.example.producity.models.source.remote.UserRemoteDataSource

object ServiceLocator {

    @Volatile
    var authRepository: IAuthRepository? = null
        @VisibleForTesting set

    @Volatile
    var userRepository: IUserRepository? = null
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

    @VisibleForTesting
    fun resetAllRepositories() {
        synchronized(Any()) {
            // Clear all data to avoid test pollution.
            authRepository = null
            userRepository = null
        }
    }

}