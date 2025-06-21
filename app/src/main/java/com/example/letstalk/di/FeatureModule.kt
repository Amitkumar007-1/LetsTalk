package com.example.letstalk.di

import com.example.letstalk.common.repository.UserRepository
import com.example.letstalk.common.service.UserService
import com.example.letstalk.data.repository.ChatRepositoryImpl
import com.example.letstalk.data.repository.HomeRepositoryImpl
import com.example.letstalk.data.repository.ProfileRepositoryImpl
import com.example.letstalk.domain.service.ChatService
import com.example.letstalk.domain.service.HomeService
import com.example.letstalk.domain.service.ProfileService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
 abstract class  FeatureModule {
    @Binds
    abstract fun provideHomeRepository(homeRepositoryImpl: HomeRepositoryImpl):HomeService
    @Binds
    abstract fun provideChatRepository(chatRepository:ChatRepositoryImpl):ChatService
    @Binds
    abstract fun provideUserRepository(userRepository: UserRepository):UserService
    @Binds
    abstract fun provideProfileRepository(profileRepository:ProfileRepositoryImpl):ProfileService

}