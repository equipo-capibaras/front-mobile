package io.capibaras.abcall

import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.SuccessUIState
import io.capibaras.abcall.viewmodels.SharedViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SharedViewModelTest {
    private lateinit var sharedViewModel: SharedViewModel

    @Before
    fun setUp() {
        sharedViewModel = SharedViewModel()
    }

    @Test
    fun `setLoadingState sets isLoading to true`() {
        sharedViewModel.setLoadingState(true)
        assertTrue(sharedViewModel.isLoading)
    }

    @Test
    fun `setLoadingState sets isLoading to false`() {
        sharedViewModel.setLoadingState(false)
        assertFalse(sharedViewModel.isLoading)
    }

    @Test
    fun `setErrorState updates errorUIState`() {
        val errorState = ErrorUIState.Error(resourceId = 1)
        sharedViewModel.setErrorState(errorState)
        assertEquals(errorState, sharedViewModel.errorUIState)
    }

    @Test
    fun `setSuccessState updates successUIState`() {
        val successState = SuccessUIState.Success(resourceId = 1)
        sharedViewModel.setSuccessState(successState)
        assertEquals(successState, sharedViewModel.successUIState)
    }

    @Test
    fun `clearErrorUIState sets errorUIState to NoError`() {
        val errorState = ErrorUIState.Error(resourceId = 1)
        sharedViewModel.setErrorState(errorState)
        sharedViewModel.clearErrorUIState()
        assertEquals(ErrorUIState.NoError, sharedViewModel.errorUIState)
    }

    @Test
    fun `clearSuccessUIState sets successUIState to NoSuccess`() {
        val successState = SuccessUIState.Success(resourceId = 1)
        sharedViewModel.setSuccessState(successState)
        sharedViewModel.clearSuccessUIState()
        assertEquals(SuccessUIState.NoSuccess, sharedViewModel.successUIState)
    }
}
