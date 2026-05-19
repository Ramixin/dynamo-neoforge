package net.ramixin.dynamo.neoforge.events.contexts;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.ramixin.stator.events.contexts.CommandRegistrationContext;

public record CommandRegistrationContextImpl(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) implements CommandRegistrationContext {

    public CommandRegistrationContextImpl(RegisterCommandsEvent event) {
        this(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }
}
