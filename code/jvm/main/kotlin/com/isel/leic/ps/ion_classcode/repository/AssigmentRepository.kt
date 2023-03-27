package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput

interface AssigmentRepository {
    fun createAssigment(assigment: AssigmentInput): Int
    fun getAssigmentById(assigmentId: Int): Assigment
    fun deleteAssigment(assigmentId: Int)
    fun updateAssigmentTitle(assigmentId: Int, title: String)
    fun updateAssigmentDescription(assigmentId: Int, description: String)
    fun updateAssigmentNumbElemsPerGroup(assigmentId: Int, numb: Int)
    fun updateAssigmentNumbGroups(assigmentId: Int, numb: Int)
}
