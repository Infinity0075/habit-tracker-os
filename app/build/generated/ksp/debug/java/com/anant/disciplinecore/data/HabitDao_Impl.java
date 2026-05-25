package com.anant.disciplinecore.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HabitDao_Impl implements HabitDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Habit> __insertionAdapterOfHabit;

  private final EntityDeletionOrUpdateAdapter<Habit> __deletionAdapterOfHabit;

  private final EntityDeletionOrUpdateAdapter<Habit> __updateAdapterOfHabit;

  private final SharedSQLiteStatement __preparedStmtOfResetStaleCompletions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllHabits;

  public HabitDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHabit = new EntityInsertionAdapter<Habit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `habits` (`id`,`name`,`emoji`,`category`,`streak`,`longestStreak`,`totalCompletions`,`isCompletedToday`,`lastCompletedDate`,`daysTracked`,`consistencyScore`,`weeklyPattern`,`createdAt`,`accentColor`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Habit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEmoji());
        statement.bindString(4, entity.getCategory());
        statement.bindLong(5, entity.getStreak());
        statement.bindLong(6, entity.getLongestStreak());
        statement.bindLong(7, entity.getTotalCompletions());
        final int _tmp = entity.isCompletedToday() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getLastCompletedDate());
        statement.bindLong(10, entity.getDaysTracked());
        statement.bindLong(11, entity.getConsistencyScore());
        statement.bindString(12, entity.getWeeklyPattern());
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindString(14, entity.getAccentColor());
        statement.bindString(15, entity.getNotes());
      }
    };
    this.__deletionAdapterOfHabit = new EntityDeletionOrUpdateAdapter<Habit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `habits` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Habit entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfHabit = new EntityDeletionOrUpdateAdapter<Habit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `habits` SET `id` = ?,`name` = ?,`emoji` = ?,`category` = ?,`streak` = ?,`longestStreak` = ?,`totalCompletions` = ?,`isCompletedToday` = ?,`lastCompletedDate` = ?,`daysTracked` = ?,`consistencyScore` = ?,`weeklyPattern` = ?,`createdAt` = ?,`accentColor` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Habit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEmoji());
        statement.bindString(4, entity.getCategory());
        statement.bindLong(5, entity.getStreak());
        statement.bindLong(6, entity.getLongestStreak());
        statement.bindLong(7, entity.getTotalCompletions());
        final int _tmp = entity.isCompletedToday() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getLastCompletedDate());
        statement.bindLong(10, entity.getDaysTracked());
        statement.bindLong(11, entity.getConsistencyScore());
        statement.bindString(12, entity.getWeeklyPattern());
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindString(14, entity.getAccentColor());
        statement.bindString(15, entity.getNotes());
        statement.bindLong(16, entity.getId());
      }
    };
    this.__preparedStmtOfResetStaleCompletions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE habits SET isCompletedToday = 0 WHERE lastCompletedDate != ? AND isCompletedToday = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllHabits = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM habits";
        return _query;
      }
    };
  }

  @Override
  public Object insertHabit(final Habit habit, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHabit.insert(habit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHabit(final Habit habit, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfHabit.handle(habit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateHabit(final Habit habit, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfHabit.handle(habit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object resetStaleCompletions(final String today,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetStaleCompletions.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, today);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfResetStaleCompletions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllHabits(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllHabits.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllHabits.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Habit>> getAllHabits() {
    final String _sql = "SELECT * FROM habits ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"habits"}, false, new Callable<List<Habit>>() {
      @Override
      @Nullable
      public List<Habit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "streak");
          final int _cursorIndexOfLongestStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "longestStreak");
          final int _cursorIndexOfTotalCompletions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCompletions");
          final int _cursorIndexOfIsCompletedToday = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompletedToday");
          final int _cursorIndexOfLastCompletedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastCompletedDate");
          final int _cursorIndexOfDaysTracked = CursorUtil.getColumnIndexOrThrow(_cursor, "daysTracked");
          final int _cursorIndexOfConsistencyScore = CursorUtil.getColumnIndexOrThrow(_cursor, "consistencyScore");
          final int _cursorIndexOfWeeklyPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyPattern");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<Habit> _result = new ArrayList<Habit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Habit _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpStreak;
            _tmpStreak = _cursor.getInt(_cursorIndexOfStreak);
            final int _tmpLongestStreak;
            _tmpLongestStreak = _cursor.getInt(_cursorIndexOfLongestStreak);
            final int _tmpTotalCompletions;
            _tmpTotalCompletions = _cursor.getInt(_cursorIndexOfTotalCompletions);
            final boolean _tmpIsCompletedToday;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompletedToday);
            _tmpIsCompletedToday = _tmp != 0;
            final String _tmpLastCompletedDate;
            _tmpLastCompletedDate = _cursor.getString(_cursorIndexOfLastCompletedDate);
            final int _tmpDaysTracked;
            _tmpDaysTracked = _cursor.getInt(_cursorIndexOfDaysTracked);
            final int _tmpConsistencyScore;
            _tmpConsistencyScore = _cursor.getInt(_cursorIndexOfConsistencyScore);
            final String _tmpWeeklyPattern;
            _tmpWeeklyPattern = _cursor.getString(_cursorIndexOfWeeklyPattern);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpAccentColor;
            _tmpAccentColor = _cursor.getString(_cursorIndexOfAccentColor);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new Habit(_tmpId,_tmpName,_tmpEmoji,_tmpCategory,_tmpStreak,_tmpLongestStreak,_tmpTotalCompletions,_tmpIsCompletedToday,_tmpLastCompletedDate,_tmpDaysTracked,_tmpConsistencyScore,_tmpWeeklyPattern,_tmpCreatedAt,_tmpAccentColor,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTotalCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM habits";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCompletedTodayCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM habits WHERE isCompletedToday = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
