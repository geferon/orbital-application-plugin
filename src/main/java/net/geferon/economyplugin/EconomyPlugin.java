package net.geferon.economyplugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.geferon.economyplugin.commands.CommandBalance;
import net.geferon.economyplugin.commands.CommandEarn;
import net.geferon.economyplugin.commands.CommandGive;
import net.geferon.economyplugin.commands.CommandSetBal;
import net.geferon.economyplugin.database.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create plugin folder if it doesn't exist
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        // Dependency injection
        BinderModule module = new BinderModule(this);
        //Injector injector = Guice.createInjector(module);
        Injector injector = module.createInjector();
        // Register commands
        getCommand("bal").setExecutor(injector.getInstance(CommandBalance.class));
        getCommand("give").setExecutor(injector.getInstance(CommandGive.class));
        getCommand("setbal").setExecutor(injector.getInstance(CommandSetBal.class));
        getCommand("earn").setExecutor(injector.getInstance(CommandEarn.class));

        injector.getInstance(Database.class).load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
