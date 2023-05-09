package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a Credits Output Model.
 */
data class CreditsOutputModel(
    val teacher: CreditsTeacher = CreditsTeacher("Pedro Félix", "pedro.felix@isel.pt","https://github.com/pmhsfelix"),
    val students: List<CreditsStudent> = listOf<CreditsStudent>(
        CreditsStudent("André Santos", 48309, "A48309@alunos.isel.pt","https://github.com/AndreSantos0"),
        CreditsStudent("Ricardo Henriques", 48322, "A48322@alunos.isel.pt","https://github.com/Henriquess19"),
        CreditsStudent("João Magalhães", 48323, "A48348@alunos.isel.pt","https://github.com/JoaoMagalhaes23"),
    )
) : OutputModel

/**
 * Represents a Credits Student Output Model.
 */
data class CreditsStudent(
    val name: String,
    val schoolNumber: Int,
    val email: String,
    val githubLink: String
)

/**
 * Represents a Credits Teacher Output Model.
 */
data class CreditsTeacher(
    val name: String,
    val email: String,
    val githubLink: String
)
