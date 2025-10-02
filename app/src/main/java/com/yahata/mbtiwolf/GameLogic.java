package com.yahata.mbtiwolf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {

    public static Map<String, GameRole> assignRoles(List<String> players, String theme, int wolfCount) {
        // テーマに応じた役職リスト取得
        List<GameRole> roles;
        if ("MBTI".equals(theme)) {
            roles = DataSource.getMbtiRoles();
        } else {
            roles = DataSource.getLoveTypeRoles();
        }

        if (players == null) {
            throw new IllegalArgumentException("プレイヤーを追加してください。");
        }
        int totalPlayers = players.size();
        if (wolfCount < 0 || wolfCount > totalPlayers) {
            throw new IllegalArgumentException("人狼がいません。");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("市民用の役職が用意されていません。");
        }

        // 役職リストをシャッフルして、"1つ選ぶ" 形にする
        List<GameRole> shuffledRoles = new ArrayList<>(roles);
        Collections.shuffle(shuffledRoles);

        int citizenCount = totalPlayers - wolfCount;

        // プレイヤーをシャッフル
        List<String> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers);

        Map<String, GameRole> assignments = new HashMap<>();

        // まず人狼を割り当てる（最初の wolfCount 人）
        for (int i = 0; i < wolfCount; i++) {
            assignments.put(
                    shuffledPlayers.get(i),
                    new GameRole("人狼", "あなたは人狼です。正体を隠してください。")
            );
        }

        // 市民がいなければ終了
        if (citizenCount <= 0) {
            return assignments;
        }

        // 市民用の役職を「ランダムに1つ」選び、残りの全員にその役職を割り当てる
        GameRole chosenCitizenRole = shuffledRoles.get(0);

        for (int i = 0; i < citizenCount; i++) {
            String player = shuffledPlayers.get(wolfCount + i);
            assignments.put(player, chosenCitizenRole);
        }

        return assignments;
    }
}
