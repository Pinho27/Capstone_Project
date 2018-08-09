package com.example.luis.capstoneproject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HeadlineDAO {

    @Query("SELECT * FROM Headline")
    List<Headline> getHeadlines();

    @Query("SELECT * from Headline WHERE url=:url")
    Headline getHeadline(String url);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHeadline(Headline headline);

    @Delete
    int deleteHeadline(Headline headline);
}
