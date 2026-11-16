package trc.synchest;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import trc.core.ITRCAddon;
import trc.core.settings.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;

public class TRCAddon implements ITRCAddon, ISettingsHolder {
    public static final String MOD_ID = "synchest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final BoolSetting SYNC_ENABLED =
            new BoolSetting("sync_named_chests", Component.translatable("synchest.setting.sync_named_chests"), true, "Sync named chests");

    static final List<Setting> settings = new ArrayList<>();

    @Override
    public void init() {
        settings.add(SYNC_ENABLED);

        SettingsManager.getSettingsManager().register(this);
    }

    @Override
    public void start() { NameTagNaming.register(); }

    @Override
    public String getModId() { return MOD_ID; }

    // Internal function for TRC-Core to collect all settings. Do not touch.
    @Override
    public List<Setting> getSettings() { return settings; }

    @Override
    public void onSettingsChanged() {}

    public static @NotNull Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }
}