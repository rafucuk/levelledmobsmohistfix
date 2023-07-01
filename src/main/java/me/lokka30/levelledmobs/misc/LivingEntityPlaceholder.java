/*
 * Copyright (c) 2020-2021  lokka30. Use of this source code is governed by the GNU AGPL v3.0 license that can be found in the LICENSE.md file.
 */

package me.lokka30.levelledmobs.misc;

import java.util.List;
import java.util.Stack;
import me.lokka30.levelledmobs.LevelledMobs;
import me.lokka30.levelledmobs.LivingEntityInterface;
import me.lokka30.levelledmobs.rules.RuleInfo;
import me.lokka30.levelledmobs.wrappers.LivingEntityWrapperBase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for the LivingEntity class that provides various common function and settings used for
 * processing rules Used only with the summon command
 *
 * @author stumper66
 * @since 3.0.0
 */
public class LivingEntityPlaceholder
    extends LivingEntityWrapperBase
    implements
    LivingEntityInterface
{

    private LivingEntityPlaceholder(final @NotNull LevelledMobs main) {
        super(main);
    }

    private EntityType entityType;
    private final static Object cachedPlaceHolders_Lock = new Object();
    private final static Stack<LivingEntityPlaceholder> cache = new Stack<>();

    @NotNull
    public static LivingEntityPlaceholder getInstance(final EntityType entityType,
        final @NotNull Location location, final @NotNull LevelledMobs main) {
        final LivingEntityPlaceholder leph;

        if (location.getWorld() == null) {
            throw new NullPointerException("World can't be null");
        }

        synchronized (cachedPlaceHolders_Lock) {
            if (cache.empty()) {
                leph = new LivingEntityPlaceholder(main);
            } else {
                leph = cache.pop();
            }
        }

        leph.populateEntityData(entityType, location, location.getWorld());
        leph.inUseCount.set(1);
        return leph;
    }

    private void populateEntityData(final EntityType entityType, final @NotNull Location location,
        final @NotNull World world) {
        this.entityType = entityType;
        super.populateData(world, location);
    }

    public void free() {
        if (inUseCount.decrementAndGet() > 0) {
            return;
        }
        if (!getIsPopulated()) {
            return;
        }

        clearEntityData();
        synchronized (cachedPlaceHolders_Lock) {
            cache.push(this);
        }
    }

    public void clearEntityData() {
        this.entityType = null;
        super.clearEntityData();
    }

    @NotNull public EntityType getEntityType() {
        if (this.entityType == null) {
            throw new NullPointerException("EntityType was null");
        }

        return this.entityType;
    }

    public @NotNull List<RuleInfo> getApplicableRules() {
        return main.rulesManager.getApplicableRules(this).allApplicableRules;
    }

    @NotNull public String getTypeName() {
        return this.entityType.name();
    }

    public void setSpawnedTimeOfDay(final int ticks) {
        this.spawnedTimeOfDay = ticks;
    }

    public int getSpawnedTimeOfDay() {
        if (this.spawnedTimeOfDay != null) {
            return this.spawnedTimeOfDay;
        }

        final int result = (int) getWorld().getTime();
        setSpawnedTimeOfDay(result);

        return result;
    }

    public Integer getSummonedLevel() {
        return summonedLevel;
    }

    public boolean isWasSummoned() {
        return summonedLevel != null;
    }
}
