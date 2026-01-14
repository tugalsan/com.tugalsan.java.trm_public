package com.tugalsan.app.table;

import com.tugalsan.app.table.sg.init.AppSGFInitPack;
import com.tugalsan.app.table.sg.init.AppSGFInitPack_ConfigTableUser;
import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.list.client.*;
import com.tugalsan.lib.rql.buffer.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.gwt.webapp.server.*;
import com.tugalsan.api.sql.conn.server.*;
import com.tugalsan.api.thread.server.async.run.TS_ThreadAsyncRun;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.time.server.TS_TimeElapsed;
import com.tugalsan.api.time.server.TS_TimeUtils;
import com.tugalsan.api.function.client.TGS_FuncUtils;
import com.tugalsan.app.table.sg.init.AppSGFInitPackUtils;
import com.tugalsan.lib.boot.server.*;
import com.tugalsan.lib.login.client.*;
import com.tugalsan.lib.login.server.*;
import com.tugalsan.lib.route.client.TGS_LibRouteLoginUtils;
import com.tugalsan.lib.rql.allow.server.*;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import javax.servlet.http.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;

public class AppSGEInitPack extends TS_SGWTExecutor {

    final private static TS_Log d = TS_Log.of(false, AppSGEInitPack.class);
//    final private static boolean ENABLE_SLOW_BUT_SAFE_LOADING = false;

    @Override
    public TS_SGWTValidationResult validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var f = (AppSGFInitPack) funcBase;
        var u_loginCard = TS_LibLoginCardUtils.get(servletKillTrigger, rq, f);
        if (u_loginCard.isExcuse()) {
            var msg = "loginCard==:" + u_loginCard.excuse().getMessage() + " -> " + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, null);
        }
        var loginCard = u_loginCard.value();
        if (loginCard.userNone) {
            var msg = "loginCard.userNone:" + funcBase.getInput_url();
            f.setExceptionMessage(msg);
            d.ce("validate", msg);
            return new TS_SGWTValidationResult(false, loginCard);
        }
        return new TS_SGWTValidationResult(true, loginCard);
    }

    @Override
    public String name() {
        return AppSGFInitPack.class.getSimpleName();
    }

    @Override
    public void run(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest rq, TGS_SGWTFuncBase funcBase, Object vldRtn) {
        var elapsed = TS_TimeElapsed.of();

        //INIT
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "INIT");
        }
        var loginCard = (TGS_LibLoginCard) vldRtn;
        var cp = TS_LibBootUtils.pck;
        var anchor = cp.sqlAnc;
        var f = (AppSGFInitPack) funcBase;
        var now = TGS_Time.of();

        //USERNAME
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "USERNAME");
        }
        if (loginCard.userNone) {
            d.ci("run", "loginCard.userNone");
            return;
        }
        var username = loginCard.userName.toString();
        d.ci("run", "loginCard.userName", username);

        //TABLENAME
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "TABLENAME");
        }
        var tableName = f.getInput_tableName();

        //USER_DATA
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "USER_DATA");
        }
        var userData = SYNC_USER_DATA.findFirst(c -> c.username.equals(username));
        if (userData == null) {
            //OPEN_TABLES
            if (d.infoEnable) {
                d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", "OPEN_TABLES");
            }
            var openTables = getAllowTableOpenSorted(cp.sqlAnc, loginCard);
            //USER TABLES
            if (d.infoEnable) {
                d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", "USER_PACKS");
            }
            var userPacks = createUserData(anchor, loginCard.userAdmin, username, openTables);
            userData = UserData.of(username, now, false, openTables, userPacks);
            SYNC_USER_DATA.add(userData);
            if (d.infoEnable) {
                d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", username, "continue with new userData");
            }
        } else {
            if (d.infoEnable) {
                d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "userData exits", username);
            }
//            if (ENABLE_SLOW_BUT_SAFE_LOADING) {
//                while (TS_LibBootUtils.killTrigger.hasNotTriggered()) {
//                    if (!userData.inProcess) {
//                        break;
//                    }
//                    d.ce("run", "userData exits", "inProcess", "waiting...");
//                    TS_ThreadWait.seconds("", TS_LibBootUtils.killTrigger, 1);
//                }
//            }
            var one_minute_later_after_last_data = userData.time.cloneIt().incrementMinute(1);
            if (d.infoEnable) {
                d.ci("run", "userData exits", now.toString_timeOnly(), "now");
                d.ci("run", "userData exits", userData.time.toString_timeOnly(), "userData.time");
                d.ci("run", "userData exits", one_minute_later_after_last_data.toString_timeOnly(), "one_minute_later_after_last_data");
            }
            if (one_minute_later_after_last_data.hasSmaller(now)) {
                TS_ThreadAsyncRun.now(TS_LibBootUtils.killTrigger(d.className(), "update_user_data"), kt -> {//update_async
                    var _userData = SYNC_USER_DATA.findFirst(c -> c.username.equals(username));//effecttive final :(
                    if (_userData == null) {
                        d.ce("run", "userData exits", username, "ASYNC_REFRESH", "SKIP", "ERROR: cannot find userData");
                        return;
                    }
                    if (_userData.inProcess) {
                        d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "ASYNC_REFRESH", "SKIP", "already in process");
                        return;
                    }
                    TGS_FuncMTCUtils.run(() -> {
                        if (d.infoEnable) {
                            d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "ASYNC_REFRESH", "LOCKED");
                        }
                        _userData.inProcess = true;
                        //OPEN_TABLES
                        if (d.infoEnable) {
                            d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "ASYNC_REFRESH", "OPEN_TABLES");
                        }
                        _userData.openTables = getAllowTableOpenSorted(cp.sqlAnc, loginCard);
                        //USER_TABLES
                        if (d.infoEnable) {
                            d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "ASYNC_REFRESH", "USER_PACKS");
                        }
                        _userData.userPacks = createUserData(anchor, loginCard.userAdmin, username, _userData.openTables);
                        _userData.time = TGS_Time.of();
                    }, e -> {
                        d.ce("run", "userData exits", username, "ASYNC_REFRESH", e.getMessage());
                    }, () -> {
                        if (d.infoEnable) {
                            d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "ASYNC_REFRESH", "RELEASED");
                        }
                        _userData.inProcess = false;
                    });
                });
            } else {
                if (d.infoEnable) {
                    d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "time is ok");
                }
            }
            if (d.infoEnable) {
                d.ci("run", "userData exits", TS_TimeUtils.toString(elapsed.elapsed_now()), username, "continue with current/volatile userData");
            }
        }

        //ALL_TABLES
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "ALL_TABLES");
        }
        var tableFound = userData.openTables.stream()
                .map(t -> t.nameSql)
                .anyMatch(tn -> Objects.equals(tn, tableName));
        List<TGS_LibRqlTbl> allTables = tableFound ? TS_LibRqlBufferUtils.items.toList_modifiable() : TGS_ListUtils.of();

        //PACK
        if (d.infoEnable) {
            d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "PACK");
        }
        var sqfPack = AppSGFInitPackUtils.toSGFPack(tableName, allTables, userData.userPacks);
        f.setInput_tableName(sqfPack.getInput_tableName());
        f.setOutput_tables(sqfPack.getOutput_tables());
        f.setOutput_userAllowFileWrite(sqfPack.getOutput_userAllowFileWrite());
        f.setOutput_userColHideIdxes(sqfPack.getOutput_userColHideIdxes());
        f.setOutput_userEditableDays(sqfPack.getOutput_userEditableDays());
        f.setOutput_userTables(sqfPack.getOutput_userTables());

        //END
        d.ci("run", TS_TimeUtils.toString(elapsed.elapsed_now()), "END");
    }

    public static void warmUp(TS_SQLConnAnchor anchor, List<String> usernames) {
        var elapsed = TS_TimeElapsed.of();
        d.cr("warmUp", "begin...");
        usernames.parallelStream().forEach(userName -> {
            var userData = SYNC_USER_DATA.findFirst(c -> c.username.equals(userName));
            if (userData == null) {
                var userAdmin = TGS_LibRouteLoginUtils.userAdmin(userName);
                //OPEN_TABLES
                d.cr("warmUp", userName, TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", "OPEN_TABLES");
                var openTables = getAllowTableOpenSorted(anchor, userAdmin, userName);
                //USER TABLES
                d.cr("warmUp", userName, TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", "USER_PACKS");
                var now = TGS_Time.of();
                var userPacks = createUserData(anchor, userAdmin, userName, openTables);
                userData = UserData.of(userName, now, false, openTables, userPacks);
                SYNC_USER_DATA.add(userData);
                d.cr("warmUp", userName, TS_TimeUtils.toString(elapsed.elapsed_now()), "userData == null", userName, "continue with new userData");
            }
        });
        d.cr("warmUp", "end");
    }

    private static List<AppSGFInitPack_ConfigTableUser> createUserData(TS_SQLConnAnchor anchor, boolean userAdmin, String username, List<TGS_LibRqlTbl> openTables) {
        List<AppSGFInitPack_ConfigTableUser> userPacks = new ArrayList();
        openTables.forEach(table -> {
            var colHideIdxes = userAdmin ? "" : TS_LibRqlAllowRowUtils.get(anchor, table.nameSql, TS_LibRqlAllowRowUtils.TYPE_COLUMNHIDE() + username).value;
            var allowFileWrie = userAdmin ? true : TS_LibRqlAllowRowUtils.get(anchor, table.nameSql, TS_LibRqlAllowRowUtils.TYPE_FILEWRITE() + username).bool;
            var editableDays = userAdmin ? -1 : TGS_CastUtils.toInt(TS_LibRqlAllowRowUtils.get(anchor, table.nameSql, TS_LibRqlAllowRowUtils.TYPE_TABLEEDIT() + username).value, 0);
            userPacks.add(AppSGFInitPack_ConfigTableUser.of(
                    table, colHideIdxes, allowFileWrie, editableDays
            ));
        });
        return userPacks;
    }

    public static List<TGS_LibRqlTbl> getAllowTableOpenSorted(TS_SQLConnAnchor anchor, TGS_LibLoginCard loginCard) {
        return getAllowTableOpenSorted(anchor, loginCard.userAdmin, loginCard.userName);
    }

    public static List<TGS_LibRqlTbl> getAllowTableOpenSorted(TS_SQLConnAnchor anchor, boolean userAdmin, CharSequence userName) {
        var tablesUnsorted = TS_LibRqlAllowTblUtils.tables(TS_LibRqlBufferUtils.items, anchor, userAdmin, userName);
        d.ci("getAllowTableOpenSorted", "tablesUnsorted.size()", tablesUnsorted.size());
        List<TGS_Tuple2<TGS_LibRqlTbl, Integer>> packTableAndIdx = TGS_ListUtils.of();
        tablesUnsorted.forEach(t -> {
            var tn = t.nameSql;
            var idx = TS_LibRqlBufferUtils.idx(tn);
            var pack = new TGS_Tuple2(t, idx);
            packTableAndIdx.add(pack);
        });
        Collections.sort(packTableAndIdx, Comparator.comparing(o -> o.value1));
        List<TGS_LibRqlTbl> tablesSorted = TGS_ListUtils.of();
        packTableAndIdx.forEach(p -> tablesSorted.add(p.value0));
        return tablesSorted;
    }

    private static class UserData {

        private UserData(String username, TGS_Time time, boolean inProcess, List<TGS_LibRqlTbl> openTables, List<AppSGFInitPack_ConfigTableUser> userPacks) {
            this.username = username;
            this.time = time;
            this.inProcess = inProcess;
            this.openTables = openTables;
            this.userPacks = userPacks;
        }

        public static UserData of(String username, TGS_Time time, boolean inProcess, List<TGS_LibRqlTbl> openTables, List<AppSGFInitPack_ConfigTableUser> userPacks) {
            return new UserData(username, time, inProcess, openTables, userPacks);
        }

        @Override
        public String toString() {
            return "UserData{" + "username=" + username + ", time=" + time + ", inProcess=" + inProcess + ", openTables=" + openTables + ", userPacks=" + userPacks + '}';
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + Objects.hashCode(this.username);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final UserData other = (UserData) obj;
            return Objects.equals(this.username, other.username);
        }

        String username;
        volatile TGS_Time time;
        volatile boolean inProcess;
        volatile List<TGS_LibRqlTbl> openTables;
        volatile List<AppSGFInitPack_ConfigTableUser> userPacks;
    }
    final public static TS_ThreadSyncLst<UserData> SYNC_USER_DATA = TS_ThreadSyncLst.ofSlowWrite();
}
