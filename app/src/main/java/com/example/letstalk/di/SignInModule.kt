package com.example.letstalk.di

import com.example.letstalk.data.repository.SignInRepositoryImpl
import com.example.letstalk.domain.service.SignInService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SignInModule {

    @Binds
   abstract fun provideSignInRepository(signInRepository:SignInRepositoryImpl) :SignInService
}