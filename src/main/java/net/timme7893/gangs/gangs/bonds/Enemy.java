package net.timme7893.gangs.gangs.bonds;

import net.timme7893.gangs.gangs.Gang;

public class Enemy {

    private Gang own;
    private Gang enemy;

    public Enemy(Gang own, Gang enemy) {
        this.own = own;
        this.enemy = enemy;
    }

    public void resolve() {

    }

    public void accept() {
        own.addEnemy(this);
        enemy.addEnemy(new Enemy(enemy,own));
    }

    public Gang getOwn() {
        return own;
    }

    public Gang getEnemy() {
        return enemy;
    }
}
