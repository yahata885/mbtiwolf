package com.yahata.mbtiwolf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {

    public static Map<String, GameRole> assignRoles(List<String> players, String theme, int wolfCount) {
        List<GameRole> roles;
        if ("MBTI".equals(theme)) {
            roles = DataSource.getMbtiRoles();
        } else {
            roles = DataSource.getLoveTypeRoles();
        }

        List<GameRole> shuffledRoles = new ArrayList<>(roles);
        Collections.shuffle(shuffledRoles);

        int citizenCount = players.size() - wolfCount;
        if (shuffledRoles.size() < citizenCount) {
            throw new IllegalArgumentException("役割の数がプレイヤー数に対して不足しています。");
        }

        List<String> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers);

        Map<String, GameRole> assignments = new HashMap<>();

        for (int i = 0; i < wolfCount; i++) {
            assignments.put(shuffledPlayers.get(i), new GameRole("人狼", "あなたは人狼です。正体を隠し、議論をかき乱してください。"));
        }

        for (int i = 0; i < citizenCount; i++) {
            String player = shuffledPlayers.get(wolfCount + i);
            GameRole role = shuffledRoles.get(i);
            assignments.put(player, role);
        }

        return assignments;
    }
}