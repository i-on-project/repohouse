package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a Home Output Model.
 */
data class HomeOutputModel(
    val title: String = "i-on ClassCode",
    val description: String = "Easy academic collaboration: Create, manage, and share projects on GitHub with ease.",
    val subDescription: String = "Perfect for faculty and students.",
    val est: String = "2023",
) : OutputModel
