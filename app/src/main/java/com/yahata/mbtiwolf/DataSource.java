package com.yahata.mbtiwolf;

import java.util.Arrays;
import java.util.List;

public class DataSource {

    public static List<GameRole> getMbtiRoles() {
        return Arrays.asList(
                new GameRole("分析家", "分析家は、「建築家」「論理学者」「指揮官」「討論者」の4つ。分析家は想像力が豊かで、知的好奇心が旺盛"),
                new GameRole("外交官", "外交官は、「提唱者」「仲介者」「主人公」「広報運動家」の4つ。外交官は人と付き合うことが得意で、仲介役やリーダー役に進んで手を挙げる"),
                new GameRole("番人", "番人は、「管理者」「擁護者」「幹部」「領事官」の4つ。空想よりも事実にもとづいた思考を好む"),
                new GameRole("探検家", "探検家は、「巨匠」「冒険家」「起業家」「エンターテイナー」の4つ。エネルギッシュで、退屈することを極端に嫌う")
        );
    }

    public static List<GameRole> getLoveTypeRoles() {
        return Arrays.asList(
                new GameRole("L×C（主導×甘えたい）", "外向的で頼れるが、実は安心感や愛情を求めやすい"),
                new GameRole("L×A（主導×受け止めたい）", "リーダーシップがあり、相手の感情や立場を尊重できる"),
                new GameRole("F×C（協調×甘えたい）", "相手にリードしてほしいと感じやすく、安心できる相手には素直に甘えられる"),
                new GameRole("F×A（協調×受け止めたい）", "聞き役やサポート役になることが多く、誠実で安心感のある関係を築きやすい")
        );
    }
}