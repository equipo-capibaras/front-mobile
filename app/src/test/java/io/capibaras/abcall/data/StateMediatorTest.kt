package io.capibaras.abcall.data

import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.utils.SuccessUIState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StateMediatorTest {
    private lateinit var stateMediator: StateMediator

    @Before
    fun setUp() {
        stateMediator = StateMediator()
    }

    @Test
    fun `setLoadingState sets isLoading to true`() {
        stateMediator.setLoadingState(true)
        assertTrue(stateMediator.isLoading)
    }

    @Test
    fun `setLoadingState sets isLoading to false`() {
        stateMediator.setLoadingState(false)
        assertFalse(stateMediator.isLoading)
    }

    @Test
    fun `setErrorState updates errorUIState`() {
        val errorState = ErrorUIState.Error(resourceId = 1)
        stateMediator.setErrorState(errorState)
        assertEquals(errorState, stateMediator.errorUIState)
    }

    @Test
    fun `setSuccessState updates successUIState`() {
        val successState = SuccessUIState.Success(resourceId = 1)
        stateMediator.setSuccessState(successState)
        assertEquals(successState, stateMediator.successUIState)
    }

    @Test
    fun `clearErrorUIState sets errorUIState to NoError`() {
        val errorState = ErrorUIState.Error(resourceId = 1)
        stateMediator.setErrorState(errorState)
        stateMediator.clearErrorUIState()
        assertEquals(ErrorUIState.NoError, stateMediator.errorUIState)
    }

    @Test
    fun `clearSuccessUIState sets successUIState to NoSuccess`() {
        val successState = SuccessUIState.Success(resourceId = 1)
        stateMediator.setSuccessState(successState)
        stateMediator.clearSuccessUIState()
        assertEquals(SuccessUIState.NoSuccess, stateMediator.successUIState)
    }
}
