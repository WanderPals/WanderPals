package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateTripViewModel
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode



class CreateSuggestionScreenTest(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateSuggestionScreenTest>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("createSuggestionScreen") }) {

    val screenTitle: KNode = onNode { hasTestTag("createSuggestionTitle") }
    val createButton: KNode = onNode { hasTestTag("createSuggestionButton") }

    val inputTitle: KNode = onNode { hasTestTag("inputSuggestionTitle") }
    val inputBudget: KNode = onNode { hasTestTag("inputSuggestionBudget") }
    val inputStartDate: KNode = onNode { hasTestTag("inputSuggestionStartDate") }
    val inputStartTime: KNode = onNode { hasTestTag("inputSuggestionStartTime") }
    val inputEndDate: KNode = onNode { hasTestTag("inputSuggestionEndDate") }
    val inputEndTime: KNode = onNode { hasTestTag("inputSuggestionEndTime") }
    val inputDescription: KNode = onNode { hasTestTag("inputSuggestionDescription") }
    val inputAddress: KNode = onNode { hasTestTag("inputSuggestionAddress") }
    val inputWebsite: KNode = onNode { hasTestTag("inputSuggestionWebsite") }
}

open class CreateTripViewModelTest(tripsRepository: TripsRepository) :
    CreateTripViewModel(tripsRepository) {
    override fun createTrip(trip: Trip) {
        assert(trip == testTrip)
    }
}
