package io.github.lokka30.levelledmobs.commands.subcommands;

import io.github.lokka30.levelledmobs.LevelledMobs;
import io.github.lokka30.levelledmobs.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InfoSubcommand implements Subcommand {

    @Override
    public void parse(LevelledMobs instance, CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("levelledmobs.command.info")) {
            if (args.length == 1) {
                String version = instance.getDescription().getVersion();
                String description = instance.getDescription().getDescription();
                List<String> supportedVersions = Utils.getSupportedServerVersions();
                List<String> contributors = Arrays.asList("stumper66", "Eyrian", "iCodinqs", "deiphiz", "konsolas", "bStats", "SpigotMC");
                String listSeparator = Objects.requireNonNull(instance.messagesCfg.getString("command.levelledmobs.info.listSeparator"), "messages.yml: command.levelledmobs.info.listSeparator is undefined");

                List<String> aboutMsg = instance.messagesCfg.getStringList("command.levelledmobs.info.about");
                aboutMsg = Utils.replaceAllInList(aboutMsg, "%version%", version);
                aboutMsg = Utils.replaceAllInList(aboutMsg, "%description%", description);
                aboutMsg = Utils.replaceAllInList(aboutMsg, "%supportedVersions%", String.join(listSeparator, supportedVersions));
                aboutMsg = Utils.replaceAllInList(aboutMsg, "%contributors%", String.join(listSeparator, contributors));
                aboutMsg.forEach(sender::sendMessage);
            } else {
                List<String> usageMsg = instance.messagesCfg.getStringList("command.levelledmobs.info.usage");
                usageMsg = Utils.replaceAllInList(usageMsg, "%prefix%", instance.configUtils.getPrefix());
                usageMsg = Utils.replaceAllInList(usageMsg, "%label%", label);
                usageMsg = Utils.colorizeAllInList(usageMsg);
                usageMsg.forEach(sender::sendMessage);
            }
        } else {
            instance.configUtils.sendNoPermissionMsg(sender);
        }
    }
}
