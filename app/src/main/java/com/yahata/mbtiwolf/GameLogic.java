package com.yahata.mbtiwolf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random; // ★追加: Randomクラスをインポート

public class GameLogic {

    /**
     * @param players   プレイヤー名リスト
     * @param theme     "MBTI" またはそれ以外
     * @param wolfCount 人狼の人数
     * @param mode      ゲームモード（Mode3 のときは市民を1種類で固定する） -- int で 3 を Mode3 と扱う
     * @return プレイヤー名 -> GameRole の割り当てマップ
     */
    public static Map<String, GameRole> assignRoles(List<String> players, String theme, int wolfCount, int mode) {
        // テーマに応じた役職リスト取得
        List<GameRole> roles;
        if ("MBTI".equals(theme)) {
            roles = DataSource.getMbtiRoles();
        } else {
            roles = DataSource.getLoveTypeRoles();
        }

        // 基本チェック
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("プレイヤーを追加してください。");
        }
        int totalPlayers = players.size();
        if (wolfCount < 0 || wolfCount > totalPlayers) {
            throw new IllegalArgumentException("人狼の数が不正です。");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("市民用の役職が用意されていません。");
        }

        // シャッフルしてランダム化
//        List<GameRole> shuffledRoles = new ArrayList<>(roles);
//        Collections.shuffle(shuffledRoles);
        // GameLogic内でシャッフルされるrolesを直接操作しないようにコピーを作成
        List<GameRole> baseCitizenRoles = new ArrayList<>(roles);

        List<String> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers);

        Map<String, GameRole> assignments = new HashMap<>();

        int citizenCount = totalPlayers - wolfCount;

        // 人狼を割り当て
        for (int i = 0; i < wolfCount; i++) {
            assignments.put(
                    shuffledPlayers.get(i),
                    new GameRole("人狼", "あなたは人狼です。正体を隠してください。")
            );
        }

        // 市民がいなければ返す
        if (citizenCount <= 0) {
            return assignments;
        }

        // Mode3 の場合：市民は同じ役職（ランダムに1つ選ぶ）を全員に割り当てる
        if (mode == 3) {
//            GameRole chosenCitizenRole = shuffledRoles.get(0); // シャッフル済みの先頭を採用
            Collections.shuffle(baseCitizenRoles); // Mode3でもシャッフルしてランダム性を高める
            GameRole chosenCitizenRole = baseCitizenRoles.get(0);
            // GameRole がミュータブルならコピーして割り当てることを検討
            for (int i = 0; i < citizenCount; i++) {
                String player = shuffledPlayers.get(wolfCount + i);
                assignments.put(player, chosenCitizenRole);
            }
            return assignments;
        }

//        // Mode3 以外：市民それぞれにランダムな役職を割り当てる（元の振る舞い）
//        if (shuffledRoles.size() < citizenCount) {
//            throw new IllegalArgumentException("役割の数がプレイヤー数に対して不足しています。");
//        }
//        for (int i = 0; i < citizenCount; i++) {
//            String player = shuffledPlayers.get(wolfCount + i);
//            GameRole role = shuffledRoles.get(i);
//            assignments.put(player, role);
//        }

        // Mode3 以外：市民それぞれにランダムな役職を割り当てる（重複を許容）
        // ★★★ 変更点はこのブロック内 ★★★
        Random random = new Random(); // ランダムなインデックスを生成するためのRandomオブジェクト
        for (int i = 0; i < citizenCount; i++) {
            String player = shuffledPlayers.get(wolfCount + i);

            // baseCitizenRolesリストからランダムなインデックスの役職を取得
            GameRole role = baseCitizenRoles.get(random.nextInt(baseCitizenRoles.size())); // ★修正

            assignments.put(player, role);
        }
        // ★★★ 変更点ここまで ★★★

        return assignments;
    }
}
