package com.yahata.mbtiwolf;

import java.util.Arrays;
import java.util.List;

public class DataSource {

    public static List<GameRole> getMbtiRoles() {
        return Arrays.asList(
                new GameRole("INFP", "理想を追い求める仲介者。内向的で感受性が豊か。"),
                new GameRole("ENTJ", "大胆な指導者。効率と計画を重視し、目標達成に意欲的。"),
                new GameRole("ESFP", "自発的でエネルギッシュなエンターテイナー。今この瞬間を楽しむ。"),
                new GameRole("ISTJ", "実用的で事実に基づいた思考の持ち主。信頼性が高い。"),
                new GameRole("ENFP", "情熱的で独創的な運動家。人との繋がりを大切にする。"),
                new GameRole("ISFJ", "非常に献身的で心の温かい擁護者。人々を守る準備ができている。")
        );
    }

    public static List<GameRole> getLoveTypeRoles() {
        return Arrays.asList(
                new GameRole("慎重派", "相手をじっくり知ってから関係を進めたいタイプ。"),
                new GameRole("直感派", "ビビッときたらすぐに行動する、フィーリング重視のタイプ。"),
                new GameRole("尽くす派", "相手のために何かをしてあげることに喜びを感じるタイプ。"),
                new GameRole("追う派", "自分から積極的にアプローチしたいタイプ。"),
                new GameRole("待つ派", "相手からのアプローチを待つのが得意なタイプ。")
        );
    }
}