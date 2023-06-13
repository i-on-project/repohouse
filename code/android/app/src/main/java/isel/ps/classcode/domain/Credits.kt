package isel.ps.classcode.domain

/**
 * Represents the Credits.
 */
data class Credits(
    val teacher: CreditsTeacher = CreditsTeacher("Pedro Félix", "pedro.felix@isel.pt", "https://github.com/pmhsfelix"),
    val students: List<CreditsStudent> = listOf<CreditsStudent>(
        CreditsStudent("André Santos", 48309, "A48309@alunos.isel.pt", "https://github.com/AndreSantos0"),
        CreditsStudent("Ricardo Henriques", 48322, "A48322@alunos.isel.pt", "https://github.com/Henriquess19"),
        CreditsStudent("João Magalhães", 48348, "A48348@alunos.isel.pt", "https://github.com/JoaoMagalhaes23"),
    ),
)

/**
 * Represents a Credits Student.
 */
data class CreditsStudent(
    val name: String,
    val schoolNumber: Int,
    val email: String,
    val githubLink: String,
)

/**
 * Represents a Credits Teacher.
 */
data class CreditsTeacher(
    val name: String,
    val email: String,
    val githubLink: String,
)
