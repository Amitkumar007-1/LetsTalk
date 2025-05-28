package com.example.letstalk.di

import com.example.letstalk.data.repository.SignInRepositoryImpl
import com.example.letstalk.data.repository.SignUpRepositoryImpl
import com.example.letstalk.domain.service.SignInService
import com.example.letstalk.domain.service.SignUpService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
   abstract fun provideSignInRepository(signInRepository:SignInRepositoryImpl) :SignInService
   @Binds
   abstract fun provideSignUpRepository(signUpRepository:SignUpRepositoryImpl):SignUpService
}