package net.timme7893.gangs.gangs.updates;

import net.timme7893.gangs.GangsPlugin;
import net.timme7893.gangs.gangs.Gang;
import net.timme7893.gangs.gangs.factories.Factory;
import net.timme7893.gangs.gangs.members.GMember;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateManager {

    private GangsPlugin gangsPlugin;

    public UpdateManager(GangsPlugin gangsPlugin) {
        this.gangsPlugin = gangsPlugin;
    }

    public void startUpdate() {

        System.out.println("[Gangs] Start data updating.");

        List<GMember> members = gangsPlugin.getPlayerManager().getGangMembers().values().stream().filter(member -> member.isDirty()).collect(Collectors.toList());
        List<Gang> gangs = gangsPlugin.getGangsManager().getGangs().values().stream().filter(gang -> gang.isDirty()).collect(Collectors.toList());
        List<Factory> factories = gangsPlugin.getFactoriesManager().getFactories().values().stream().filter(factory -> factory.isDirty()).collect(Collectors.toList());

        for (Factory factory : factories) {
            if (factory.getOwner() != null) {
                gangsPlugin.getFactoriesManager().updateFactory(factory.getId(), factory.getOwner().getId(), factory.getProductionRate(), factory.getBlockQuality());
            } else {
                gangsPlugin.getFactoriesManager().updateFactory(factory.getId(), 0, factory.getProductionRate(), factory.getBlockQuality());
            }
            factory.setDirty(false);
        }

        for (GMember member : members) {
            gangsPlugin.getPlayerManager().updateMember(member);
            member.setDirty(false);
        }

        for (Gang gang: gangsPlugin.getGangsManager().getGangs().values()) {
            gang.getStorageMenu().saveData();
        }
        for (Gang gang : gangs) {
            gangsPlugin.getGangsManager().updateGang(gang);
            gang.setDirty(false);
        }

        System.out.println("[Gangs] Data updated.");

    }

}
