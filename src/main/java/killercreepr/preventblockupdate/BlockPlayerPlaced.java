package killercreepr.preventblockupdate;

import killerceepr.cplayerblocks.api.PlayerBlocksAPI;
import org.bukkit.block.Block;

public class BlockPlayerPlaced {
    public static boolean placedByPlayer(Block b){
        return PlayerBlocksAPI.api().wasPlacedByPlayer(b);
    }
}
