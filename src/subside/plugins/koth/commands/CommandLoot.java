package subside.plugins.koth.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.LootAlreadyExistException;
import subside.plugins.koth.exceptions.LootNotExistException;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandLoot implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).build());
        }

        Player player = (Player) sender;

        if (args.length < 1 || !Perm.Admin.LOOT.has(sender)) {
            Utils.sendMsg(player, 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot list").commandInfo("List loot chests").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot create <loot>").commandInfo("Create loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot edit <loot>").commandInfo("Edit loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth remove <loot>").commandInfo("Remove loot chest").build());
            return;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if (args[0].equalsIgnoreCase("create")) {
            create(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("edit")){
            edit(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("list")){
            list(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("remove")){
            remove(sender, newArgs);
        } else {
            Utils.sendMsg(player, 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot list").commandInfo("List loot chests").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot create <loot>").commandInfo("Create loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/loot edit <loot>").commandInfo("Edit loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth remove <loot>").commandInfo("Remove loot chest").build());
        }
    }
    
    private void create(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth loot create <loot>").build());
        }
        
        Loot loot = KothHandler.getLoot(args[0]);
        if(loot != null){
            throw new LootAlreadyExistException();
        }
        
        KothHandler.getLoots().add(new Loot(args[0]));
        LootLoader.save();
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CREATE).build());
    }
    
    private void edit(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth loot edit <loot>").build());
        }
        
        Loot loot = KothHandler.getLoot(args[0]);
        if(loot == null){
            throw new LootNotExistException();
        }
        
        ((Player)sender).openInventory(loot.getInventory());
        
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_OPENING).loot(loot.getName()).build());
    }
    
    private void list(CommandSender sender, String[] args){
        new MessageBuilder(Lang.COMMAND_LISTS_LOOT_TITLE).buildAndSend(sender);
        for (Loot loot : KothHandler.getLoots()) {
            new MessageBuilder(Lang.COMMAND_LISTS_LOOT_ENTRY).loot(loot.getName()).buildAndSend(sender);
        }
    }

    private void remove(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth loot remove <loot>").build());
        }
        
        Loot loot = KothHandler.getLoot(args[0]);
        if(loot == null){
            throw new LootNotExistException();
        }
        
        KothHandler.getLoots().remove(loot);
        LootLoader.save();
        
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_REMOVE).loot(loot.getName()).build());
    }
    
    @Override
    public IPerm getPermission() {
        return Perm.LOOT2;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "loot"
        };
    }

}