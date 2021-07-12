package net.timme7893.gangs.gangs.riots;

import net.timme7893.gangs.gangs.Gang;
import org.bukkit.block.Block;

public class GangLab {

    private Gang owner;
    private Block block;

    public GangLab(Gang owner, Block block) {
        this.owner = owner;
        this.block = block;
    }

    public Gang getOwner() {
        return owner;
    }

    public Block getBlock() {
        return block;
    }
}
