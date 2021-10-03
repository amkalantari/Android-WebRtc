package com.core.db


import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.core.dto.tables.TestDb
import io.reactivex.Completable

@Dao
interface DaoTest {

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(projects: TestDb)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(projects: TestDb): Long

    @Query("select count(*) as num from testDb")
    fun selectProductsCount(): LiveData<Int>

    @Delete
    fun remove(project: TestDb)

    @Query("delete from testDb")
    fun clear(): Completable

    @Query("select * from testDb")
    fun select(): PagingSource<Int, TestDb>

    @Query("select * from testDb")
    fun selectAll(): LiveData<List<TestDb>>

}