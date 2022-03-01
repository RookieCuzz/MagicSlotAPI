package pku.yim.magiclibs.magicslotapi.slot.impl;

import pku.yim.magiclibs.magicslotapi.MagicSlotAPI;
import pku.yim.magiclibs.magicslotapi.event.SlotUpdateEvent;
import pku.yim.magiclibs.magicslotapi.event.UpdateTrigger;
import pku.yim.magiclibs.magicslotapi.slot.PlayerSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class DragonCoreSlot extends PlayerSlot {

    private final String identifier;

    public DragonCoreSlot(String identifier) {
        super("DRAGON_CORE_" + identifier, "DRAGON_CORE_SLOT");
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAsyncSafe() {
        return true;
    }

    @Override
    public void get(Player player, Consumer<ItemStack> callback) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(MagicSlotAPI.getPlugin(), () -> {
                MagicSlotAPI.getDragonCoreHook().getItemFromSlot(player, identifier, callback);
            });
        } else {
            MagicSlotAPI.getDragonCoreHook().getItemFromSlot(player, identifier, callback);
        }
    }

    @Override
    public void set(Player player, ItemStack item, Consumer<Boolean> callback) {
        MagicSlotAPI.getDragonCoreHook().setItemToSlot(player, identifier, item,
                result -> {
                    if (result) {
                        SlotUpdateEvent updateEvent = new SlotUpdateEvent(UpdateTrigger.SET, player, this, null, item);
                        updateEvent.setUpdateImmediately();
                        Bukkit.getPluginManager().callEvent(updateEvent);
                    }
                    callback.accept(result);
                });
    }
}
