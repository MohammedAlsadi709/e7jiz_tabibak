package com.alsadimoh.graduationproject

import com.alsadimoh.graduationproject.retrofit.ApiService
import com.alsadimoh.graduationproject.retrofit.models.auth.LoginModel
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyUnitTest {

    // test for 3 things-> view model .. repository .. apiService
    // this test only for apiService


    companion object {
        lateinit var apiService: ApiService
        private const val BASE_URL = "https://hajez.mymatgar.com/api/"
        private const val phone = "0597653709"
        private const val password = "password"

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build().create(ApiService::class.java)
            print("beforeClass")
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            print("afterClass")
        }
    }

    @Before
    fun beforeLogin() {
        print("before login")
    }

    @Test
    fun testLogin() {
        Assert.assertNotNull(apiService)
        runBlocking {
         val result = apiService.login(LoginModel(phone, password))
            Assert.assertTrue(result.status)
            Assert.assertEquals("أحمد أبو أحمد",result.items.name)
        }
    }

    @After
    fun afterLogin() {
        print("after login")
    }


    @Before
    fun beforeGetMostRatedDoctors() {
        print("before GetMostRatedDoctors")
    }

    @Test
    fun testGetMostRatedDoctors() {
        Assert.assertNotNull(apiService)
        runBlocking {
            val result = apiService.getAllRatedDoctors("1") // page number 1
            Assert.assertTrue(result.status)
            Assert.assertTrue(result.items.isNotEmpty()) // array of doctors is not empty
        }
    }

    @After
    fun afterGetMostRatedDoctors() {
        print("after GetMostRatedDoctors")
    }

}