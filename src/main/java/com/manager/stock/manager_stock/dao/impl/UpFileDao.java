package com.manager.stock.manager_stock.dao.impl;

import org.h2.util.json.JSONObject;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UpFileDao extends AbstractDao<Integer>{
    DateTimeFormatter fomart = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public int getStatus() {
        String sql = "select * from upfilestatus where id = ?";
        List<Integer> status = query(sql, rs -> rs.getInt("STATUS"), fomart.format(LocalDate.now()) + "");
        if(status.isEmpty()) {
            return -1;
        }
        return status.get(0);
    }

    public void insert() {
        String sql = "insert into upfilestatus(id, status) values(?, ?);";
        List<Object[]> params = new ArrayList<>();
        params.add(new Object[] {fomart.format(LocalDate.now()), 1});
        save(sql, params);
    }

    public void backup() {
        try {
            Statement stmt = DatasourceInitialize.getInstance().createStatement();
            stmt.execute("BACKUP TO 'C:/data/backup.zip'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
