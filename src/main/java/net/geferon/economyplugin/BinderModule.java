package net.geferon.economyplugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import net.geferon.economyplugin.database.Database;
import net.geferon.economyplugin.database.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

public class BinderModule extends AbstractModule {
    private final EconomyPlugin plugin;

    public BinderModule(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(EconomyPlugin.class).toInstance(this.plugin);
        this.bind(JavaPlugin.class).to(EconomyPlugin.class);
        this.bind(Database.class).to(SQLite.class).in(Scopes.SINGLETON);
    }
}
