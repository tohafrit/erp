package ru.korundm.dao.view

interface CommonViewService<T> {

    fun getAll(): List<T>
    fun getAllByIdList(idList: List<Long>): List<T>
    fun read(id: Long): T
}