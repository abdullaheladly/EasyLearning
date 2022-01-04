package com.abdullah996.easylearning.di

import com.abdullah996.easylearning.adapters.SubjectsRowAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object FirestoreModule {

    @Singleton
    @Provides
    fun provideFirestoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }


}