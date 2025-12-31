package killercreepr.preventblockupdate;

import killerceepr.utility.api.item.ItemPredicate;
import killerceepr.utility.command.CruxCmd;
import killerceepr.utility.config.CruxConfig;
import killerceepr.utility.config.data.FileBlockPredicate;
import killerceepr.utility.config.data.FileItemPredicate;
import killerceepr.utility.plugin.CruxPlugin;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PreventBlockUpdate extends CruxPlugin implements Listener {
    private static PreventBlockUpdate instance;
    public static PreventBlockUpdate inst(){
        return instance;
    }

    protected final Collection<CustomPlace> customPlace = new HashSet<>();

    public CustomPlace customPlaceByItem(ItemStack item){
        for (CustomPlace place : customPlace) {
            if(place.getItem().test(item)) return place;
        }
        return null;
    }
    public CustomPlace customPlaceByPlaceableOn(Block block){
        BlockState state = block.getState();
        return customPlaceByPlaceableOn(state);
    }
    public CustomPlace customPlaceByPlaceableOn(BlockState state){
        for (CustomPlace place : customPlace) {
            if(place.getPlaceableOn().test(state)) return place;
        }
        return null;
    }
    public CustomPlace customPlaceByPlaced(BlockState state){
        for (CustomPlace place : customPlace) {
            if(place.getPlacedBlock().test(state)) return place;
        }
        return null;
    }
    public CustomPlace customPlaceByPlaced(Block block){
        return customPlaceByPlaced(block.getState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(physics(event.getBlock())){
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        String s = getConfig().getString("custom_place_permission");
        if(s != null && !p.hasPermission(s)) return;

        Block b = event.getClickedBlock();
        if(!event.getAction().isRightClick()) return;
        if(b == null) return;

        ItemStack item = event.getItem();
        if(item == null) return;
        CustomPlace customPlace = customPlaceByItem(item);
        if(customPlace == null) return;
        if(!customPlace.getPlaceableOn().test(b.getState())) return;

        for(int i = 1; i <= customPlace.getBlockSize(); i++){
            Block above = b.getRelative(0, i, 0);
            if(!above.isEmpty()) return;
        }

        for(int i = 0; i < customPlace.getBlockSize(); i++){
            BlockData data = customPlace.getPlaceBlockData().get(i);
            Block above = b.getRelative(0, i+1, 0);
            above.setType(data.getMaterial(), false);
            above.setBlockData(data, false);
        }
    }


    public boolean shouldCancelPhysics(Block b){
        if(getConfig().getBoolean("allow_only_player_placed_physics")){
            return !BlockPlayerPlaced.placedByPlayer(b);
        }

        CustomPlace customPlace = customPlaceByPlaced(b);
        if(customPlace == null) return false;
        return customPlace.placedCheckUnder(b);
    }

    public boolean physics(Block b){
        return shouldCancelPhysics(b);
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void enabled() {
        saveDefaultConfig();
        super.enabled();

        registerListeners(
            this
        );
    }

    @Override
    public void reloadConfigs() {
        super.reloadConfigs();
        reloadConfig();

        customPlace.clear();

        CruxConfig cfg = new CruxConfig(this, "config");

        var section = cfg.config().getConfigurationSection("custom_place");
        if(section == null) return;

        section.getKeys(false).forEach(s ->{
            String path = "custom_place." + s + ".";
            ItemPredicate item = new FileItemPredicate().get(cfg, path + "item");
            if(item == null) return;
            List<String> block = section.getStringList(s + ".block");
            if(block.isEmpty()){
                String x = section.getString(s + ".item");
                if(x != null) block = List.of(x);
                else block = section.getStringList(s + ".item");
            }

            var customPlace = new CustomPlace(
                item,
                new FileBlockPredicate().get(cfg, path + "placeable_on"),
                block
            );
            this.customPlace.add(customPlace);
        });
    }

    @Override
    public void disabled() {
        super.disabled();

        HandlerList.unregisterAll((Plugin) this);
    }

    @Override
    public void registerDefaultCmds() {
        registerCmd(
            new CruxCmd() {
                @Override
                public void command(@NotNull CommandSender sender, @NotNull String[] strings) {
                    reloadConfigs();
                    sender.sendMessage("Reloaded config.");
                }

                @Override
                public @NotNull String cmd() {
                    return "preventblockupdate";
                }

                @Override
                public @Nullable String subAction() {
                    return "reload";
                }
            }
        );
    }
}
