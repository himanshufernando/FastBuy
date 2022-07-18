package project.superuniqueit.fastbuy.repo


import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import project.superuniqueit.fastbuy.data.db.FastBuyDatabase
import project.superuniqueit.fastbuy.data.db.UserDao
import project.superuniqueit.fastbuy.services.network.api.APIInterface
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {


    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        FastBuyDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideChatDao(db: FastBuyDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideHomeRepository(aPIInterface: APIInterface,userDao : UserDao): HomeRepo{
        return HomeRepo(
            client = aPIInterface,
            userDao = userDao
        )
    }
}

