package owmii.powah.compat.rei;

import java.util.Collection;
import java.util.List;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.plugins.REIPluginProvider;

// On Fabric, REI plugins are registered via fabric.mod.json entrypoint "rei-plugins-client"
// The @REIPluginLoaderClient annotation is NeoForge/Forge-only and has been removed.
public class PowahREIPluginProvider implements REIPluginProvider<PowahREIPlugin> {
    @Override
    public Collection<PowahREIPlugin> provide() {
        return List.of(new PowahREIPlugin());
    }

    @Override
    public Class<PowahREIPlugin> getPluginProviderClass() {
        return PowahREIPlugin.class;
    }
}
