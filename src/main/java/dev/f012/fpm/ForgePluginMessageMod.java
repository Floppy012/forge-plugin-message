package dev.f012.fpm;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgePluginMessageMod.MODID)
public class ForgePluginMessageMod
{
    public static final String MODID = "forge-plugin-message";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ForgePluginMessageMod(FMLJavaModLoadingContext context)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Registering /pluginmsg command");
        event.getServer().getCommands().getDispatcher().register(
          Commands.literal("pluginmsg")
            .then(Commands.argument("player", EntityArgument.player())
              .then(Commands.argument("messageType", StringArgumentType.word())
                .then(Commands.argument("args", StringArgumentType.greedyString()).executes((ctx) ->
                  this.runPluginMsg(
                    ctx,
                    EntityArgument.getPlayer(ctx, "player"),
                    StringArgumentType.getString(ctx, "messageType"),
                    StringArgumentType.getString(ctx, "args").split(" ")
                  )
                ))
                .executes((ctx) ->
                  this.runPluginMsg(
                    ctx,
                    EntityArgument.getPlayer(ctx, "player"),
                    StringArgumentType.getString(ctx, "messageType"),
                    new String[0]
                  )
                )
              )
            )
        );
    }

    private int runPluginMsg(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String messageType, String[] args) {
        if (!ctx.getSource().hasPermission(2)) {
            ctx.getSource().sendFailure(Component.literal("Operator Level 2 required to run this command!"));
            return 1;
        }

        ByteArrayDataOutput payload = ByteStreams.newDataOutput();
        payload.writeUTF(messageType);
        for (String arg : args) {
            payload.writeUTF(arg);
        }

        ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(
          ResourceLocation.fromNamespaceAndPath("bungeecord", "main"),
          new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.toByteArray()))
        );

        player.connection.send(packet);

        return 0;
    }
}
