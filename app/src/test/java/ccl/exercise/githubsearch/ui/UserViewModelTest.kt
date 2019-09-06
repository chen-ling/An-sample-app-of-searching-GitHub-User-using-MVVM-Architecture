import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import ccl.exercise.githubsearch.ui.UserViewModel
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.rules.TestRule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import retrofit2.HttpException
import retrofit2.Response

val mockAppModules = module {

    single {
        mock(GithubSearchService::class.java)
    }

    factory<HttpException> { (errStr: String) ->
        val errBody = mock(ResponseBody::class.java)
        whenever(errBody.string()).thenReturn(errStr)

        val errResponse = mock(Response::class.java)
        whenever(errResponse.errorBody()).thenReturn(errBody)

        val httpException = mock(HttpException::class.java)
        whenever(httpException.response()).thenReturn(errResponse)
        httpException
    }

    viewModel { UserViewModel() }
}

class ViewModelTest : KoinTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val service: GithubSearchService by inject()
    private val viewModel: UserViewModel by inject()

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
        whenever(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.just(mockResult))
        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)
        assertFalse(viewModel.isLoading.value ?: true)
        assertFalse(viewModel.noMoreItem.value ?: true)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `can get second page`() {
        val term = "a"
        val mockResult = SearchResponse<User>(1, listOf(mock(User::class.java)))
        val mockResult2 = SearchResponse<User>(1, listOf(mock(User::class.java)))
        whenever(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.just(mockResult))
        whenever(service.getSearchUserList(anyString(), eq(2))).thenReturn(Single.just(mockResult2))

        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)

        viewModel.loadMore()
        verify(service).getSearchUserList(eq(term), eq(2))
        assertEquals(mockResult2.item[0], viewModel.userList.value?.get(mockResult.item.size)?.user)
        assertFalse(viewModel.noMoreItem.value ?: true)
        assertFalse(viewModel.isLoading.value ?: true)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `user list cleared when keyword changes`() {
        val term1 = "a"
        val term2 = "b"
        val mockResult = SearchResponse<User>(1, listOf(mock(User::class.java)))
        val mockResult2 = SearchResponse<User>(1, listOf(mock(User::class.java)))
        whenever(service.getSearchUserList(eq(term1), eq(1))).thenReturn(Single.just(mockResult))
        whenever(service.getSearchUserList(eq(term2), eq(1))).thenReturn(Single.just(mockResult2))
        viewModel.search(term1)
        verify(service).getSearchUserList(eq(term1), eq(1))
        assertEquals(mockResult.item[0], viewModel.userList.value?.get(0)?.user)

        viewModel.search(term2)
        verify(service).getSearchUserList(eq(term2), eq(1))
        assertEquals(mockResult2.item[0], viewModel.userList.value?.get(0)?.user)
        assertFalse(viewModel.isLoading.value ?: true)
        assertFalse(viewModel.noMoreItem.value ?: true)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `does not trigger load more when no more items`() {
        val term = "a"
        val emptyResult = SearchResponse<User>(0, listOf())
        whenever(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.just(emptyResult))
        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertFalse(viewModel.isLoading.value ?: true)
        assert(viewModel.noMoreItem.value ?: false)
        viewModel.loadMore()
        verifyNoMoreInteractions(service)
    }

    @Test
    fun `when error, can extract error body`() {
        val term = "a"
        val errStr = "this is a fake error"
        val httpException: HttpException = get { parametersOf(errStr) }
        whenever(service.getSearchUserList(anyString(), eq(1))).thenReturn(Single.error(httpException))
        viewModel.search(term)
        verify(service).getSearchUserList(eq(term), eq(1))
        assertFalse(viewModel.isLoading.value ?: true)
        assertEquals(errStr, viewModel.loadingError.value?.message)
        verifyNoMoreInteractions(service)
    }
}