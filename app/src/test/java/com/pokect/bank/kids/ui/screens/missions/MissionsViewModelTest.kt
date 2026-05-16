package com.pokect.bank.kids.ui.screens.missions

import com.pokect.bank.kids.data.models.MissionCategory
import com.pokect.bank.kids.data.models.MissionStatus
import com.pokect.bank.kids.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MissionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads all missions from repository`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)
        val state = vm.uiState.value

        assertEquals(4, state.allMissions.size)
        assertEquals(4, state.filteredMissions.size)
    }

    @Test
    fun `filter ALL returns all missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)

        vm.selectFilter(MissionFilterCategory.ALL)

        assertEquals(4, vm.uiState.value.filteredMissions.size)
    }

    @Test
    fun `filter DAILY returns only DAILY category missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)

        vm.selectFilter(MissionFilterCategory.DAILY)

        val filtered = vm.uiState.value.filteredMissions
        assertTrue(filtered.all { it.category == MissionCategory.DAILY })
    }

    @Test
    fun `filter WEEKLY returns only WEEKLY category missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)

        vm.selectFilter(MissionFilterCategory.WEEKLY)

        val filtered = vm.uiState.value.filteredMissions
        assertTrue(filtered.all { it.category == MissionCategory.WEEKLY })
    }

    @Test
    fun `filter SPECIAL returns only SPECIAL category missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)

        vm.selectFilter(MissionFilterCategory.SPECIAL)

        val filtered = vm.uiState.value.filteredMissions
        assertTrue(filtered.all { it.category == MissionCategory.SPECIAL })
    }

    @Test
    fun `Mission model has required fields`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val mission = repo.getMissions().first()

        assertTrue(mission.icon.isNotEmpty())
        assertTrue(mission.description.isNotEmpty())
        assertTrue(mission.difficulty.name.isNotEmpty())
        assertTrue(mission.progress >= 0)
        assertTrue(mission.xp > 0)
        assertTrue(mission.coins >= 0)
    }

    @Test
    fun `MissionStatus has 4 states`() {
        assertEquals(4, MissionStatus.entries.size)
        assertTrue(MissionStatus.entries.contains(MissionStatus.LOCKED))
        assertTrue(MissionStatus.entries.contains(MissionStatus.AVAILABLE))
        assertTrue(MissionStatus.entries.contains(MissionStatus.IN_PROGRESS))
        assertTrue(MissionStatus.entries.contains(MissionStatus.COMPLETED))
    }

    @Test
    fun `MissionCategory has 3 values`() {
        assertEquals(3, MissionCategory.entries.size)
        assertTrue(MissionCategory.entries.contains(MissionCategory.DAILY))
        assertTrue(MissionCategory.entries.contains(MissionCategory.WEEKLY))
        assertTrue(MissionCategory.entries.contains(MissionCategory.SPECIAL))
    }

    @Test
    fun `MissionFilterCategory has ALL plus 3 categories`() {
        assertEquals(4, MissionFilterCategory.entries.size)
    }

    @Test
    fun `MissionsChanged event reloads missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = MissionsViewModel(repo)
        val initialCount = vm.uiState.value.allMissions.size

        // Join weekly challenge which creates a new mission and emits MissionsChanged
        repo.joinWeeklyChallenge("wc1")

        // Wait for the event to propagate and ViewModel to reload
        kotlinx.coroutines.delay(200)

        assertEquals(initialCount + 1, vm.uiState.value.allMissions.size)
    }
}
