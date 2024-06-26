package com.github.se.wanderpals.model.data

import androidx.compose.ui.graphics.Color

/** Categories of expenses that can be used to classify each expense record. */
enum class Category(val nameToDisplay: String, val color: Color) {
  TRANSPORT("Transport", Color(0xFF5B61D6)),
  ACCOMMODATION("Accommodation", Color(0xFFCF4D6F)),
  ACTIVITIES("Activities", Color(0xFFAE59DC)),
  FOOD("Food", Color(0xFFFFA500)),
  OTHER("Other", Color(0xFF30BCED))
}
