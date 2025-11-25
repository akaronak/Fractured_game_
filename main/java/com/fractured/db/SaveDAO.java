package com.fractured.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.fractured.core.GameState;
import com.fractured.model.Player;

@SuppressWarnings("unused")
public class SaveDAO {
    private final DBUtil dbUtil;

    public SaveDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void saveGame(String saveName, GameState state) throws SQLException {
        String sql = "INSERT INTO saves(save_name, player_name, current_scene, inventory) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Player p = state.getPlayer();
            String invCsv = String.join(",", p.getInventory().list());
            ps.setString(1, saveName);
            ps.setString(2, p.getName());
            ps.setString(3, state.getCurrentScene());
            ps.setString(4, invCsv);
            ps.executeUpdate();
        }
    }

    public GameState loadLatestForPlayer(String playerName) throws SQLException {
        String sql = "SELECT * FROM saves WHERE player_name = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Player p = new Player(rs.getString("player_name"));
                String inv = rs.getString("inventory");
                if (inv != null && !inv.isEmpty()) {
                    Arrays.stream(inv.split(",")).forEach(s -> {
                        if (!s.isBlank()) p.getInventory().add(s.trim());
                    });
                }
                
                GameState gs = new GameState(p);
                gs.setCurrentScene(rs.getString("current_scene"));
                return gs;
            }
        }
    }
}
