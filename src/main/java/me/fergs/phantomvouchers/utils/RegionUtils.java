package me.fergs.phantomvouchers.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Set;

public class RegionUtils {
    public static boolean isPlayerInBlacklistedRegion(Player player, Set<String> blacklistedRegions) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        Location location = player.getLocation();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        Set<ProtectedRegion> regionsIt = set.getRegions();

        for (ProtectedRegion region : regionsIt) {
            if (blacklistedRegions.contains(region.getId())) {
                return true;
            }
        }

        return false;
    }
}
