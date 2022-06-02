package net.minecraftforge.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.advancement.Achievement;

public class AchievementPage {
    private String name;
    private LinkedList<Achievement> achievements;
    private static LinkedList<AchievementPage> achievementPages = new LinkedList();

    public AchievementPage(String name, Achievement... achievements) {
        this.name = name;
        this.achievements = new LinkedList(Arrays.asList(achievements));
    }

    public String getName() {
        return this.name;
    }

    public List<Achievement> getAchievements() {
        return this.achievements;
    }

    public static void registerAchievementPage(AchievementPage page) {
        if (getAchievementPage(page.getName()) != null) {
            throw new RuntimeException("Duplicate achievement page name \"" + page.getName() + "\"!");
        } else {
            achievementPages.add(page);
        }
    }

    public static AchievementPage getAchievementPage(int index) {
        return (AchievementPage)achievementPages.get(index);
    }

    public static AchievementPage getAchievementPage(String name) {
        for(AchievementPage page : achievementPages) {
            if (page.getName().equals(name)) {
                return page;
            }
        }

        return null;
    }

    public static Set<AchievementPage> getAchievementPages() {
        return new HashSet(achievementPages);
    }

    public static boolean isAchievementInPages(Achievement achievement) {
        for(AchievementPage page : achievementPages) {
            if (page.getAchievements().contains(achievement)) {
                return true;
            }
        }

        return false;
    }

    public static String getTitle(int index) {
        return index == -1 ? "Minecraft" : getAchievementPage(index).getName();
    }
}
