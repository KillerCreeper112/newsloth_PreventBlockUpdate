package killercreepr.preventblockupdate;

import killerceepr.utility.api.block.BlockPredicate;
import killerceepr.utility.api.item.ItemPredicate;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class CustomPlace {
    protected final ItemPredicate item;
    protected final BlockPredicate placeableOn;
    protected final List<String> block;
    protected final BlockPredicate placedBlock;
    protected final List<BlockData> placeBlockData;

    public CustomPlace(ItemPredicate item, BlockPredicate placeableOn, List<String> block) {
        this.item = item;
        this.placeableOn = placeableOn;
        this.block = block;
        this.placedBlock = (world, blockState) -> {
            String s = blockState.getBlockData().getAsString();
            for (String blockData : block) {
                if(blockData.equals(s)) return true;
            }
            return false;
        };
        this.placeBlockData = new ArrayList<>();
        for (String s : this.block) {
            this.placeBlockData.add(Bukkit.createBlockData(s));
        }
    }

    public List<BlockData> getPlaceBlockData() {
        return placeBlockData;
    }

    public boolean placedCheckUnder(Block b){
        Block ground = b.getRelative(BlockFace.DOWN);
        var state = ground.getState();
        if(placeableOn.test(state)) return true;
        if(placedBlock.test(state)) return true;
        return false;
    }

    public BlockPredicate getPlacedBlock() {
        return placedBlock;
    }

    public int getBlockSize(){
        return block.size();
    }

    public ItemPredicate getItem() {
        return item;
    }

    public BlockPredicate getPlaceableOn() {
        return placeableOn;
    }

    public List<String> getBlock() {
        return block;
    }
}
