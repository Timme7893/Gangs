package net.timme7893.gangs.gangs.bonds;

import net.timme7893.gangs.gangs.Gang;

public class Ally {

    private Gang own;
    private Gang ally;

    public Ally(Gang own, Gang ally) {
        this.own = own;
        this.ally = ally;
    }

    public void accept() {
        own.addAlly(this);
        ally.addAlly(new Ally(ally,own));
    }

    public Gang getOwn() {
        return own;
    }

    public Gang getAlly() {
        return ally;
    }
}
