package com.anant.disciplinecore.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.anant.disciplinecore.data.local.entities.UserProgress;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProgressDao_Impl implements UserProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProgress> __insertionAdapterOfUserProgress;

  private final SharedSQLiteStatement __preparedStmtOfUpdateXpAndLevel;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStreak;

  private final SharedSQLiteStatement __preparedStmtOfAddFocusMinutes;

  private final SharedSQLiteStatement __preparedStmtOfIncrementHabitsCompleted;

  public UserProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProgress = new EntityInsertionAdapter<UserProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_progress` (`id`,`xp`,`level`,`totalXpEarned`,`currentStreak`,`bestStreak`,`totalHabitsCompleted`,`totalFocusMinutes`,`lastActiveDate`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProgress entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getXp());
        statement.bindLong(3, entity.getLevel());
        statement.bindLong(4, entity.getTotalXpEarned());
        statement.bindLong(5, entity.getCurrentStreak());
        statement.bindLong(6, entity.getBestStreak());
        statement.bindLong(7, entity.getTotalHabitsCompleted());
        statement.bindLong(8, entity.getTotalFocusMinutes());
        statement.bindString(9, entity.getLastActiveDate());
      }
    };
    this.__preparedStmtOfUpdateXpAndLevel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE user_progress\n"
                + "        SET xp = ?,\n"
                + "            level = ?,\n"
                + "            totalXpEarned = ?\n"
                + "        WHERE id = 1\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStreak = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE user_progress\n"
                + "        SET currentStreak = ?,\n"
                + "            bestStreak = ?,\n"
                + "            lastActiveDate = ?\n"
                + "        WHERE id = 1\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfAddFocusMinutes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE user_progress\n"
                + "        SET totalFocusMinutes = totalFocusMinutes + ?\n"
                + "        WHERE id = 1\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementHabitsCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE user_progress\n"
                + "        SET totalHabitsCompleted =\n"
                + "            totalHabitsCompleted + 1\n"
                + "        WHERE id = 1\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final UserProgress progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProgress.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateXpAndLevel(final int xp, final int level, final int totalXpEarned,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateXpAndLevel.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, xp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, level);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, totalXpEarned);
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
          __preparedStmtOfUpdateXpAndLevel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStreak(final int streak, final int best, final String date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStreak.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, streak);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, best);
        _argIndex = 3;
        _stmt.bindString(_argIndex, date);
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
          __preparedStmtOfUpdateStreak.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addFocusMinutes(final int minutes, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddFocusMinutes.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, minutes);
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
          __preparedStmtOfAddFocusMinutes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementHabitsCompleted(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementHabitsCompleted.acquire();
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
          __preparedStmtOfIncrementHabitsCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<UserProgress> getProgress() {
    final String _sql = "SELECT * FROM user_progress WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"user_progress"}, false, new Callable<UserProgress>() {
      @Override
      @Nullable
      public UserProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfXp = CursorUtil.getColumnIndexOrThrow(_cursor, "xp");
          final int _cursorIndexOfLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "level");
          final int _cursorIndexOfTotalXpEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "totalXpEarned");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfBestStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "bestStreak");
          final int _cursorIndexOfTotalHabitsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHabitsCompleted");
          final int _cursorIndexOfTotalFocusMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFocusMinutes");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final UserProgress _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpXp;
            _tmpXp = _cursor.getInt(_cursorIndexOfXp);
            final int _tmpLevel;
            _tmpLevel = _cursor.getInt(_cursorIndexOfLevel);
            final int _tmpTotalXpEarned;
            _tmpTotalXpEarned = _cursor.getInt(_cursorIndexOfTotalXpEarned);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpBestStreak;
            _tmpBestStreak = _cursor.getInt(_cursorIndexOfBestStreak);
            final int _tmpTotalHabitsCompleted;
            _tmpTotalHabitsCompleted = _cursor.getInt(_cursorIndexOfTotalHabitsCompleted);
            final int _tmpTotalFocusMinutes;
            _tmpTotalFocusMinutes = _cursor.getInt(_cursorIndexOfTotalFocusMinutes);
            final String _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getString(_cursorIndexOfLastActiveDate);
            _result = new UserProgress(_tmpId,_tmpXp,_tmpLevel,_tmpTotalXpEarned,_tmpCurrentStreak,_tmpBestStreak,_tmpTotalHabitsCompleted,_tmpTotalFocusMinutes,_tmpLastActiveDate);
          } else {
            _result = null;
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
  public Object getProgressOnce(final Continuation<? super UserProgress> $completion) {
    final String _sql = "SELECT * FROM user_progress WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProgress>() {
      @Override
      @Nullable
      public UserProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfXp = CursorUtil.getColumnIndexOrThrow(_cursor, "xp");
          final int _cursorIndexOfLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "level");
          final int _cursorIndexOfTotalXpEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "totalXpEarned");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfBestStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "bestStreak");
          final int _cursorIndexOfTotalHabitsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "totalHabitsCompleted");
          final int _cursorIndexOfTotalFocusMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFocusMinutes");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final UserProgress _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpXp;
            _tmpXp = _cursor.getInt(_cursorIndexOfXp);
            final int _tmpLevel;
            _tmpLevel = _cursor.getInt(_cursorIndexOfLevel);
            final int _tmpTotalXpEarned;
            _tmpTotalXpEarned = _cursor.getInt(_cursorIndexOfTotalXpEarned);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpBestStreak;
            _tmpBestStreak = _cursor.getInt(_cursorIndexOfBestStreak);
            final int _tmpTotalHabitsCompleted;
            _tmpTotalHabitsCompleted = _cursor.getInt(_cursorIndexOfTotalHabitsCompleted);
            final int _tmpTotalFocusMinutes;
            _tmpTotalFocusMinutes = _cursor.getInt(_cursorIndexOfTotalFocusMinutes);
            final String _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getString(_cursorIndexOfLastActiveDate);
            _result = new UserProgress(_tmpId,_tmpXp,_tmpLevel,_tmpTotalXpEarned,_tmpCurrentStreak,_tmpBestStreak,_tmpTotalHabitsCompleted,_tmpTotalFocusMinutes,_tmpLastActiveDate);
          } else {
            _result = null;
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
