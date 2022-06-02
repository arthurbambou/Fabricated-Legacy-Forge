package net.minecraftforge.client.event.sound;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundSystem;

public class PlayBackgroundMusicEvent extends SoundResultEvent {
    public PlayBackgroundMusicEvent(SoundSystem manager, Sound entry) {
        super(manager, entry, null, 0.0F, 0.0F);
    }
}
