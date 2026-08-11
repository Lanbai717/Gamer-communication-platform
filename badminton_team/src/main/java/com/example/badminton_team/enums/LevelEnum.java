package com.example.badminton_team.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public class LevelEnum {
    public static final Map<Integer, String> MAP = new LinkedHashMap<>();
    static {
        MAP.put(0, "国际级运动健将");
        MAP.put(1, "运动健将");
        MAP.put(2, "一级运动员");
        MAP.put(3, "二级运动员");
        MAP.put(4, "三级运动员");
        MAP.put(5, "业余高级");
        MAP.put(6, "业余中级");
        MAP.put(7, "业余初级");
    }

    /** 数字转中文 */
    public static String toText(int level) {
        return MAP.getOrDefault(level, "业余初级");
    }

    /** 中文转数字（用于已有数据兼容） */
    public static int toInt(String text) {
        if (text == null || text.trim().isEmpty()) return 7;
        // 1. 先尝试匹配中文名称
        for (Map.Entry<Integer, String> entry : MAP.entrySet()) {
            if (entry.getValue().equals(text)) return entry.getKey();
        }
        // 2. 再尝试解析数字字符串
        try {
            int level = Integer.parseInt(text.trim());
            if (level >= 0 && level <= 7) return level;
        } catch (NumberFormatException ignored) {}
        // 3. 都不匹配则返回默认业余初级
        return 7;
    }
}