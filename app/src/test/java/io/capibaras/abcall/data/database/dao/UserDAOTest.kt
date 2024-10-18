package io.capibaras.abcall.data.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.database.models.User
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class UserDAOTest {
    private lateinit var database: ABCallDB
    private lateinit var userDao: UserDAO
    private lateinit var faker: Faker

    @Before
    fun setup() {
        faker = Faker()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ABCallDB::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `test refresh replaces the old user with the new one`() = runBlocking {
        val oldUser = User(
            id = faker.random.nextUUID(),
            clientId = faker.random.nextUUID(),
            name = faker.name.name(),
            email = faker.internet.safeEmail(),
            clientName = faker.company.name()
        )

        val newUser = User(
            id = faker.random.nextUUID(),
            clientId = faker.random.nextUUID(),
            name = faker.name.name(),
            email = faker.internet.safeEmail(),
            clientName = faker.company.name()
        )

        userDao.insertUser(oldUser)
        assertEquals(oldUser, userDao.getUserInfo())

        userDao.refreshUser(newUser)
        assertEquals(newUser, userDao.getUserInfo())
    }
}