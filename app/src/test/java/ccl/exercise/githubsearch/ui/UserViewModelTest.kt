import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import ccl.exercise.githubsearch.ui.UserViewModel
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.rules.TestRule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock


val mockAppModules = module {

    single {
        mock(GithubSearchService::class.java)
    }

    viewModel { UserViewModel() }
}

class AppTestModules : KoinTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    val service: GithubSearchService by inject()
    val viewModel: UserViewModel by inject()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        }
    }

    @Before
    fun before() {
        startKoin {
            modules(mockAppModules)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `can get first page`() {
        val term = "a"
        val mockResult = SearchResponse<User>(1, listOf(mock(User::class.java)))
        Mockito.`when`(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.just(mockResult))
        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)
        assertEquals(false, viewModel.isLoading.value)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `can get second page`() {
        val term = "a"
        val mockResult = SearchResponse<User>(1, listOf(mock(User::class.java)))
        val mockResult2 = SearchResponse<User>(1, listOf(mock(User::class.java)))
        Mockito.`when`(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.just(mockResult))
        Mockito.`when`(service.getSearchUserList(anyString(), eq(2))).thenReturn(Single.just(mockResult2))

        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)

        viewModel.loadMore()
        verify(service).getSearchUserList(eq(term), eq(2))
        assertEquals(mockResult2.item[0], viewModel.userList.value?.get(mockResult.item.size)?.user)

        assertEquals(false, viewModel.isLoading.value)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `user list cleared when keyword changes`() {
        val term1 = "a"
        val term2 = "b"
        val mockResult = SearchResponse<User>(1, listOf(mock(User::class.java)))
        val mockResult2 = SearchResponse<User>(1, listOf(mock(User::class.java)))
        Mockito.`when`(service.getSearchUserList(eq(term1), eq(1))).thenReturn(Single.just(mockResult))
        Mockito.`when`(service.getSearchUserList(eq(term2), eq(1))).thenReturn(Single.just(mockResult2))
        viewModel.search(term1)
        verify(service).getSearchUserList(eq(term1), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)

        viewModel.search(term2)
        verify(service).getSearchUserList(eq(term2), eq(1))
        assertEquals(mockResult2.item[0], viewModel.userList.value?.get(0)?.user)
        assertEquals(false, viewModel.isLoading.value)
        verifyNoMoreInteractions(service)
    }
}