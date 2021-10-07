package project.ceyloninnovationlabs.fastbuy.repo


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.ceyloninnovationlabs.fastbuy.services.network.api.APIInterface
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideHomeRepository(aPIInterface: APIInterface): HomeRepo{
        return HomeRepo(
            client = aPIInterface
        )
    }
}

