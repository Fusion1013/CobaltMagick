package se.fusion1013.cobaltmagick.enchantments.duel;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Entity;

public record DuelState(Entity owner, Entity victim, int level, long startTimestamp, BossBar bossBar) {
}
